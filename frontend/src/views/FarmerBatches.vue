<template>
  <div class="batch-page">
    <header class="topbar">
      <div class="brand">
        <router-link to="/farmer">田野档案</router-link>
        <span>农户工作台 / 溯源批次</span>
      </div>
      <div class="account">
        <span v-if="account">Hi，{{ account.displayName || account.username }}</span>
        <router-link to="/farmer">返回工作台</router-link>
      </div>
    </header>

    <main class="main">
      <div class="page-heading">
        <div>
          <p class="eyebrow">农户溯源管理</p>
          <h1>我的溯源批次</h1>
          <p>每个批次关联一个本人产品和一个本人基地；提交后等待管理员审核。</p>
        </div>
        <button class="primary" type="button" :disabled="loading || !canCreate" @click="openCreate">新增批次</button>
      </div>

      <p class="notice">当前开发阶段仅使用模拟数据。创建和提交不代表已审核、已发布或质检合格。</p>
      <p v-if="error" class="alert" role="alert">{{ error }}</p>
      <p v-if="!loading && !canCreate" class="tip">请先创建并启用至少一个种植基地和一条农产品档案。</p>
      <div v-if="loading" class="empty">正在读取批次数据...</div>
      <div v-else-if="!page.records.length" class="empty">当前账号暂无批次，点击“新增批次”开始录入</div>

      <div v-else class="batch-list">
        <article v-for="item in page.records" :key="item.id" class="batch-card">
          <div class="card-heading">
            <div>
              <span class="eyebrow">{{ item.batchNo }}</span>
              <h2>{{ item.productName }}</h2>
            </div>
            <span :class="['state', item.status.toLowerCase()]">{{ statusText(item.status) }}</span>
          </div>
          <div class="details">
            <div><span>种植基地</span><strong>{{ item.baseName }}</strong></div>
            <div><span>种植日期</span><strong>{{ item.plantingDate || '未填写' }}</strong></div>
            <div><span>预计采收</span><strong>{{ item.plannedHarvestDate || '未填写' }}</strong></div>
            <div><span>预计产量</span><strong>{{ item.estimatedQuantityKg == null ? '未填写' : item.estimatedQuantityKg + ' kg' }}</strong></div>
          </div>
          <p v-if="item.remark" class="remark">{{ item.remark }}</p>
          <p class="meta">溯源码：{{ item.traceCode }}（尚未发布时不可对游客查询）</p>
          <div class="actions">
            <button v-if="canEdit(item)" type="button" @click="openEdit(item)">编辑草稿</button>
            <button v-if="item.status === 'DRAFT'" type="button" class="primary"
                    :disabled="submittingId === item.id" @click="submit(item)">
              {{ submittingId === item.id ? '提交中...' : '提交管理员审核' }}
            </button>
            <span v-else-if="item.status === 'REJECTED'" class="hint">修改后可重新提交</span>
            <span v-else-if="item.status === 'SUBMITTED'" class="hint">正在等待审核，暂不能编辑</span>
            <span v-else class="hint">已发布批次不可直接修改</span>
          </div>
        </article>
      </div>

      <div v-if="page.total > page.pageSize" class="pager">
        <button type="button" :disabled="loading || page.pageNum <= 1" @click="load(page.pageNum - 1)">上一页</button>
        <span>第 {{ page.pageNum }} / {{ Math.ceil(page.total / page.pageSize) }} 页</span>
        <button type="button" :disabled="loading || page.pageNum * page.pageSize >= page.total" @click="load(page.pageNum + 1)">下一页</button>
      </div>
    </main>

    <div v-if="editing" class="overlay" @click.self="closeForm">
      <div class="dialog" role="dialog" aria-modal="true" aria-labelledby="batch-title">
        <div class="dialog-heading">
          <h2 id="batch-title">{{ editId ? '编辑批次' : '新建溯源批次' }}</h2>
          <button type="button" :disabled="saving" @click="closeForm">关闭</button>
        </div>
        <form @submit.prevent="save">
          <div class="twocol">
            <label>农产品档案
              <select v-model.number="form.productId" required>
                <option :value="null" disabled>请选择本人产品</option>
                <option v-for="item in productChoices" :key="item.id" :value="item.id">{{ item.label }}</option>
              </select>
            </label>
            <label>种植基地
              <select v-model.number="form.baseId" required>
                <option :value="null" disabled>请选择本人基地</option>
                <option v-for="item in baseChoices" :key="item.id" :value="item.id">{{ item.label }}</option>
              </select>
            </label>
          </div>
          <div class="twocol">
            <label>种植日期（提交审核前必填）
              <input v-model="form.plantingDate" type="date" />
            </label>
            <label>预计采收日期
              <input v-model="form.plannedHarvestDate" type="date" />
            </label>
          </div>
          <label>预计产量（kg）
            <input v-model="form.estimatedQuantityKg" type="number" min="0" max="9999999999.99" step="0.01" placeholder="选填" />
          </label>
          <label>批次备注
            <textarea v-model.trim="form.remark" maxlength="500" rows="4" placeholder="选填：记录批次基本情况，不填写未经核实的质检结论" />
          </label>
          <p v-if="formError" class="alert" role="alert">{{ formError }}</p>
          <button type="submit" class="primary submit" :disabled="saving">{{ saving ? '保存中...' : '保存为草稿' }}</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { currentUser } from '../api/auth';
import {
  listBatchOptions, listFarmerBatches, createFarmerBatch,
  updateFarmerBatch, submitFarmerBatch
} from '../api/farmerBatches';

const blank = () => ({
  productId: null, baseId: null, plantingDate: '', plannedHarvestDate: '',
  estimatedQuantityKg: '', remark: ''
});

export default {
  name: 'FarmerBatches',
  data() {
    return {
      account: null, loading: true, saving: false, editing: false,
      editId: null, submittingId: null, form: blank(), formError: '', error: '',
      options: { products: [], bases: [] }, original: null,
      page: { total: 0, pageNum: 1, pageSize: 10, records: [] }
    };
  },
  computed: {
    canCreate() { return this.options.products.length > 0 && this.options.bases.length > 0; },
    productChoices() { return this.withExisting(this.options.products, this.original?.productId, this.original?.productName); },
    baseChoices() { return this.withExisting(this.options.bases, this.original?.baseId, this.original?.baseName); }
  },
  mounted() { this.start(); },
  methods: {
    statusText(value) {
      return { DRAFT: '草稿', SUBMITTED: '待审核', REJECTED: '已驳回', PUBLISHED: '已发布' }[value] || value;
    },
    withExisting(choices, id, label) {
      if (!id || choices.some(item => item.id === id)) return choices;
      return [...choices, { id, label: `${label}（已停用，保存前请改选）` }];
    },
    canEdit(item) { return ['DRAFT', 'REJECTED'].includes(item.status); },
    async start() {
      try {
        this.account = await currentUser();
        if (this.account.roleCode !== 'FARMER') {
          await this.$router.replace('/manage');
          return;
        }
        await this.load(1);
      } catch (error) {
        this.error = error.message || '工作台加载失败';
        this.loading = false;
      }
    },
    async load(pageNum = 1) {
      this.loading = true;
      this.error = '';
      try {
        const [options, page] = await Promise.all([listBatchOptions(), listFarmerBatches(pageNum, 10)]);
        this.options = options;
        this.page = page;
      } catch (error) {
        this.error = error.message || '批次列表加载失败';
      } finally {
        this.loading = false;
      }
    },
    openCreate() {
      this.editId = null;
      this.original = null;
      this.form = blank();
      this.formError = '';
      this.editing = true;
    },
    openEdit(item) {
      this.editId = item.id;
      this.original = item;
      this.form = {
        productId: item.productId, baseId: item.baseId,
        plantingDate: item.plantingDate || '',
        plannedHarvestDate: item.plannedHarvestDate || '',
        estimatedQuantityKg: item.estimatedQuantityKg ?? '',
        remark: item.remark || ''
      };
      this.formError = '';
      this.editing = true;
    },
    closeForm() { if (!this.saving) this.editing = false; },
    async save() {
      if (this.saving) return;
      this.saving = true;
      this.formError = '';
      try {
        if (!this.form.productId || !this.form.baseId) throw new Error('请选择产品和基地');
        if (this.form.plantingDate && this.form.plannedHarvestDate &&
            this.form.plannedHarvestDate < this.form.plantingDate) {
          throw new Error('预计采收日期不能早于种植日期');
        }
        const payload = {
          productId: this.form.productId,
          baseId: this.form.baseId,
          plantingDate: this.form.plantingDate || null,
          plannedHarvestDate: this.form.plannedHarvestDate || null,
          estimatedQuantityKg: this.form.estimatedQuantityKg === '' ? null : Number(this.form.estimatedQuantityKg),
          remark: this.form.remark || null
        };
        if (this.editId) await updateFarmerBatch(this.editId, payload);
        else await createFarmerBatch(payload);
        this.editing = false;
        await this.load(this.editId ? this.page.pageNum : 1);
      } catch (error) {
        this.formError = error.message || '保存失败';
      } finally {
        this.saving = false;
      }
    },
    async submit(item) {
      if (this.submittingId) return;
      if (!item.plantingDate) {
        this.error = '请先编辑批次，填写种植日期后再提交审核';
        return;
      }
      if (!window.confirm(`确认提交批次“${item.batchNo}”供管理员审核？提交后暂不能修改。`)) return;
      this.submittingId = item.id;
      this.error = '';
      try {
        await submitFarmerBatch(item.id);
        await this.load(this.page.pageNum);
      } catch (error) {
        this.error = error.message || '提交审核失败';
      } finally {
        this.submittingId = null;
      }
    }
  }
};
</script>

<style scoped>
.batch-page { min-height:100vh; background:#f7f8f4; color:#234a3c; }
.topbar { display:flex; align-items:center; justify-content:space-between; gap:16px; padding:20px 5%; border-bottom:1px solid #e0e9df; background:#fff; }
.brand,.account { display:flex; align-items:center; gap:18px; font-size:14px; }
.brand a { color:#234a3c; font-size:19px; text-decoration:none; font-weight:700; }.brand span { color:#819482; }
.account a { color:#447356; text-decoration:none; }
.main { max-width:1140px; margin:auto; padding:38px 24px; }.page-heading,.card-heading,.dialog-heading { display:flex; align-items:center; justify-content:space-between; gap:18px; }
.eyebrow { font-size:13px; color:#7c9786; margin:0 0 8px; }.page-heading h1 { font-size:29px; margin:0 0 8px; }.page-heading p:not(.eyebrow) { font-size:14px; color:#78907e; margin:0; }
button { padding:10px 15px; border:1px solid #d2dfd2; border-radius:9px; color:#315943; background:white; cursor:pointer; font:inherit; }
button:disabled { opacity:.55; cursor:not-allowed; }.primary { background:#234a3c; color:white; font-weight:600; border-color:#234a3c; }
.notice,.tip { margin:23px 0 10px; padding:13px 17px; background:#f5f0e2; border:1px solid #e9e2c7; border-radius:9px; color:#806942; font-size:13px; }
.alert { color:#a13c34; background:#fff0ee; padding:13px; border-radius:8px; margin:18px 0; }
.empty { margin-top:20px; padding:54px 18px; text-align:center; background:#fff; border:1px solid #e1e9e0; border-radius:12px; color:#809583; }
.batch-list { display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:16px; margin-top:24px; }
.batch-card { background:#fff; border:1px solid #e0e9df; border-radius:13px; padding:23px; min-width:0; }
.card-heading { align-items:start; }.card-heading h2 { font-size:20px; margin:8px 0 17px; }.state { font-size:12px; white-space:nowrap; border-radius:18px; padding:6px 11px; }
.state.draft { background:#f0f3ed; color:#506956; }.state.submitted { background:#f6eed7; color:#846c2c; }.state.rejected { background:#fbeae5; color:#95443c; }.state.published { background:#e2f0e4; color:#33674e; }
.details { display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:16px; }.details div { min-width:0; }.details span { display:block; color:#839787; font-size:12px; margin-bottom:6px; }.details strong { font-size:14px; overflow-wrap:anywhere; }
.remark { line-height:1.7; color:#53705a; font-size:13px; white-space:pre-wrap; }.meta { font-size:12px; color:#809383; overflow-wrap:anywhere; margin:18px 0 0; }
.actions { display:flex; flex-wrap:wrap; align-items:center; gap:12px; margin-top:22px; }.hint { color:#819583; font-size:13px; }
.pager { display:flex; justify-content:flex-end; align-items:center; gap:12px; margin:24px 0; font-size:13px; }
.overlay { position:fixed; inset:0; display:flex; align-items:center; justify-content:center; z-index:100; background:#1b2b25a8; padding:14px; box-sizing:border-box; }
.dialog { width:min(680px,100%); max-height:94vh; overflow:auto; padding:28px; border-radius:15px; background:#fff; box-sizing:border-box; }
.dialog-heading { margin-bottom:22px; }.dialog-heading h2 { margin:0; font-size:25px; }form { display:grid; gap:17px; }
label { display:grid; gap:8px; font-size:14px; } input,select,textarea { width:100%; box-sizing:border-box; padding:12px 13px; border:1px solid #d0ded0; border-radius:9px; font:inherit; color:#234a3c; background:#fff; }
textarea { resize:vertical; }.submit { padding:14px; }.twocol { display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:14px; }
@media (max-width:760px) { .topbar,.brand,.account { flex-wrap:wrap; }.main { padding:24px 15px; }.page-heading { flex-direction:column; align-items:start; }.batch-list,.twocol { grid-template-columns:1fr; }.dialog { padding:20px; } }
</style>

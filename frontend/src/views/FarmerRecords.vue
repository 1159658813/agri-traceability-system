<template>
  <div class="records-page">
    <header class="topbar">
      <router-link class="logo" to="/farmer">田野档案</router-link>
      <div class="top-actions">
        <span v-if="account">Hi，{{ account.displayName || account.username }}</span>
        <router-link to="/farmer/batches">返回批次</router-link>
      </div>
    </header>

    <main class="content">
      <div class="heading">
        <div>
          <p class="eyebrow">我的溯源批次 / 生产过程记录</p>
          <h1>农事与采收记录</h1>
          <p>记录实际发生的作业与采收情况，不填写虚构的检测或认证结论。</p>
        </div>
        <button type="button" @click="load" :disabled="loading">刷新记录</button>
      </div>
      <p class="demo-tip">当前为开发测试环境。演示数据请明确标记为【演示】；已提交或已发布的批次只允许查看。</p>
      <p v-if="error" class="alert" role="alert">{{ error }}</p>
      <p v-if="loading" class="empty">正在读取生产记录...</p>
      <template v-else-if="batch">
        <section class="batch-summary">
          <div>
            <p class="eyebrow">{{ batch.batchNo }}</p>
            <h2>{{ batch.productName }}</h2>
            <p>种植日期：{{ batch.plantingDate || '未填写' }}</p>
          </div>
          <span :class="['state', batch.status.toLowerCase()]">{{ statusText(batch.status) }}</span>
        </section>
        <p v-if="!editable" class="read-only">当前批次处于“{{ statusText(batch.status) }}”状态，记录已锁定。如需修正，请通过管理员审核流程处理。</p>

        <section class="record-section">
          <div class="section-heading">
            <div><h2>农事记录</h2><p>施肥、灌溉、田间巡查等作业可以分次登记。</p></div>
            <button v-if="editable" class="primary" type="button" @click="openFarm()">新增农事记录</button>
          </div>
          <div v-if="!farmRecords.length" class="empty">暂无农事记录</div>
          <div v-else class="record-list">
            <article v-for="item in farmRecords" :key="item.id" class="record-card">
              <div class="card-top"><strong>{{ item.operationType }}</strong><span>{{ displayTime(item.operatedAt) }}</span></div>
              <p class="record-description">{{ item.description }}</p>
              <div class="record-footer">
                <span>操作人：{{ item.operatorName || '未填写' }}</span>
                <button v-if="editable" type="button" @click="openFarm(item)">编辑</button>
              </div>
            </article>
          </div>
        </section>

        <section class="record-section">
          <div class="section-heading">
            <div><h2>采收记录</h2><p>同一批次可按实际情况登记多次采收。</p></div>
            <button v-if="editable" class="primary" type="button" @click="openHarvest()">新增采收记录</button>
          </div>
          <div v-if="!harvestRecords.length" class="empty">暂无采收记录</div>
          <div v-else class="record-list">
            <article v-for="item in harvestRecords" :key="item.id" class="record-card">
              <div class="card-top"><strong>{{ item.quantityKg }} kg</strong><span>{{ displayTime(item.harvestedAt) }}</span></div>
              <p v-if="item.grade">等级：{{ item.grade }}</p>
              <p v-if="item.remark" class="record-description">{{ item.remark }}</p>
              <div class="record-footer">
                <span>采收记录 #{{ item.id }}</span>
                <button v-if="editable" type="button" @click="openHarvest(item)">编辑</button>
              </div>
            </article>
          </div>
        </section>
      </template>
    </main>

    <div v-if="dialog" class="overlay" @click.self="closeDialog">
      <div class="dialog" role="dialog" aria-modal="true" aria-labelledby="record-title">
        <div class="dialog-heading">
          <h2 id="record-title">{{ dialogTitle }}</h2>
          <button type="button" :disabled="saving" @click="closeDialog">关闭</button>
        </div>
        <form v-if="dialog === 'farm'" @submit.prevent="saveFarm">
          <label>农事作业类型
            <select v-model="farmForm.operationType" required>
              <option value="" disabled>请选择作业类型</option>
              <option v-for="type in operationTypes" :key="type" :value="type">{{ type }}</option>
            </select>
          </label>
          <label>作业时间
            <input v-model="farmForm.operatedAt" type="datetime-local" required />
          </label>
          <label>作业内容
            <textarea v-model.trim="farmForm.description" required maxlength="1000" rows="4"
                      placeholder="如实填写本次作业情况" />
          </label>
          <label>操作人
            <input v-model.trim="farmForm.operatorName" maxlength="80" placeholder="选填" />
          </label>
          <p v-if="formError" class="alert" role="alert">{{ formError }}</p>
          <button class="primary submit" type="submit" :disabled="saving">{{ saving ? '保存中...' : '保存记录' }}</button>
        </form>
        <form v-else @submit.prevent="saveHarvest">
          <label>采收时间
            <input v-model="harvestForm.harvestedAt" type="datetime-local" required />
          </label>
          <label>采收数量（kg）
            <input v-model="harvestForm.quantityKg" type="number" min="0.01" max="9999999999.99"
                   step="0.01" required placeholder="如：25.50" />
          </label>
          <label>产品等级
            <input v-model.trim="harvestForm.grade" maxlength="40" placeholder="选填，未经评定请留空" />
          </label>
          <label>采收备注
            <textarea v-model.trim="harvestForm.remark" maxlength="500" rows="3" placeholder="选填" />
          </label>
          <p v-if="formError" class="alert" role="alert">{{ formError }}</p>
          <button class="primary submit" type="submit" :disabled="saving">{{ saving ? '保存中...' : '保存记录' }}</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { currentUser } from '../api/auth';
import { getBatchRecords, addFarmWork, editFarmWork, addHarvest, editHarvest } from '../api/farmerRecords';

const emptyFarm = () => ({ operationType: '', operatedAt: '', description: '', operatorName: '' });
const emptyHarvest = () => ({ harvestedAt: '', quantityKg: '', grade: '', remark: '' });

export default {
  name: 'FarmerRecords',
  data() {
    return {
      account: null, batch: null, farmRecords: [], harvestRecords: [],
      loading: true, saving: false, error: '', formError: '', dialog: '', editId: null,
      farmForm: emptyFarm(), harvestForm: emptyHarvest(),
      operationTypes: ['播种', '育苗', '灌溉', '施肥', '病虫害防治', '田间巡查', '其他']
    };
  },
  computed: {
    batchId() { return this.$route.params.batchId; },
    editable() { return this.batch && ['DRAFT', 'REJECTED'].includes(this.batch.status); },
    dialogTitle() {
      return `${this.editId ? '编辑' : '新增'}${this.dialog === 'farm' ? '农事' : '采收'}记录`;
    }
  },
  mounted() { this.start(); },
  methods: {
    statusText(status) {
      return { DRAFT: '草稿', SUBMITTED: '待审核', REJECTED: '已驳回', PUBLISHED: '已发布' }[status] || status;
    },
    displayTime(value) { return value ? value.replace('T', ' ').slice(0, 16) : '—'; },
    formTime(value) { return value ? value.slice(0, 16) : ''; },
    async start() {
      try {
        this.account = await currentUser();
        if (this.account.roleCode !== 'FARMER') {
          await this.$router.replace('/manage');
          return;
        }
        await this.load();
      } catch (error) {
        this.error = error.message || '页面加载失败';
        this.loading = false;
      }
    },
    async load() {
      this.loading = true;
      this.error = '';
      try {
        const data = await getBatchRecords(this.batchId);
        this.batch = data.batch;
        this.farmRecords = data.farmRecords || [];
        this.harvestRecords = data.harvestRecords || [];
      } catch (error) {
        this.error = error.message || '读取记录失败';
      } finally {
        this.loading = false;
      }
    },
    openFarm(item) {
      if (!this.editable) return;
      this.editId = item ? item.id : null;
      this.farmForm = item ? {
        operationType: item.operationType, operatedAt: this.formTime(item.operatedAt),
        description: item.description || '', operatorName: item.operatorName || ''
      } : emptyFarm();
      this.formError = '';
      this.dialog = 'farm';
    },
    openHarvest(item) {
      if (!this.editable) return;
      this.editId = item ? item.id : null;
      this.harvestForm = item ? {
        harvestedAt: this.formTime(item.harvestedAt), quantityKg: String(item.quantityKg),
        grade: item.grade || '', remark: item.remark || ''
      } : emptyHarvest();
      this.formError = '';
      this.dialog = 'harvest';
    },
    closeDialog() { if (!this.saving) this.dialog = ''; },
    async saveFarm() {
      if (this.saving) return;
      this.saving = true;
      this.formError = '';
      try {
        if (!this.farmForm.operatedAt || !this.farmForm.operationType || !this.farmForm.description.trim()) {
          throw new Error('请完整填写农事类型、时间和作业内容');
        }
        if (this.editId) await editFarmWork(this.batchId, this.editId, this.farmForm);
        else await addFarmWork(this.batchId, this.farmForm);
        this.dialog = '';
        await this.load();
      } catch (error) {
        this.formError = error.message || '农事记录保存失败';
      } finally {
        this.saving = false;
      }
    },
    async saveHarvest() {
      if (this.saving) return;
      this.saving = true;
      this.formError = '';
      try {
        const amount = Number(this.harvestForm.quantityKg);
        if (!this.harvestForm.harvestedAt || !Number.isFinite(amount) || amount <= 0) {
          throw new Error('请填写正确的采收时间和数量');
        }
        const payload = { ...this.harvestForm, quantityKg: amount };
        if (this.editId) await editHarvest(this.batchId, this.editId, payload);
        else await addHarvest(this.batchId, payload);
        this.dialog = '';
        await this.load();
      } catch (error) {
        this.formError = error.message || '采收记录保存失败';
      } finally {
        this.saving = false;
      }
    }
  }
};
</script>

<style scoped>
.records-page { min-height:100vh; color:#234a3c; background:#f7f8f4; }
.topbar { background:#fff; border-bottom:1px solid #e1e9df; padding:19px max(24px, calc((100vw - 1160px) / 2)); display:flex; align-items:center; justify-content:space-between; gap:20px; }
.logo { font-size:19px; font-weight:700; text-decoration:none; color:#234a3c; }
.top-actions { display:flex; gap:20px; align-items:center; font-size:14px; }
.top-actions a { color:#315943; text-decoration:none; }
.content { max-width:1120px; padding:36px 20px 80px; margin:auto; }
.heading,.section-heading,.card-top,.record-footer,.dialog-heading { display:flex; align-items:center; justify-content:space-between; gap:14px; }
.heading h1 { margin:7px 0; font-size:30px; }
.heading p:not(.eyebrow),.section-heading p { color:#819483; font-size:14px; margin:4px 0; }
.eyebrow { font-size:13px; color:#879b8a; margin:0; }
button { background:#fff; color:#315943; border:1px solid #d0dfd2; border-radius:9px; font:inherit; cursor:pointer; padding:10px 15px; }
button:disabled { opacity:.6; cursor:not-allowed; }
.primary { color:#fff; background:#234a3c; border-color:#234a3c; }
.demo-tip,.read-only { padding:13px 17px; border:1px solid #ece2c5; background:#f7f1e3; border-radius:9px; color:#806b46; font-size:13px; margin:24px 0; }
.alert { padding:13px; color:#a53b32; background:#fcefeb; border-radius:8px; }
.batch-summary { background:#25483c; color:#fff; border-radius:14px; padding:26px 32px; display:flex; align-items:flex-start; justify-content:space-between; margin:22px 0; gap:20px; }
.batch-summary h2 { font-size:23px; margin:8px 0 12px; }
.batch-summary p:not(.eyebrow) { color:#d2dfd5; font-size:13px; }
.state { border-radius:20px; background:#dcebdc; padding:7px 12px; color:#29513b; font-size:12px; white-space:nowrap; }
.state.submitted { background:#f4ebcf; color:#8a692d; }.state.rejected { background:#fbe6e2; color:#8d423b; }
.record-section { background:#fff; border:1px solid #e0e8df; border-radius:13px; margin-top:20px; padding:24px; }
.section-heading h2 { font-size:21px; margin:0; }
.empty { color:#829684; padding:34px 12px; text-align:center; }
.record-list { display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:14px; margin-top:22px; }
.record-card { border:1px solid #e5ebe3; border-radius:9px; padding:20px; }
.card-top span,.record-footer { color:#819483; font-size:13px; }.record-description { color:#466554; line-height:1.75; white-space:pre-wrap; }
.record-footer { border-top:1px solid #eef2ed; padding-top:12px; margin-top:17px; }.record-footer button { padding:6px 12px; font-size:12px; }
.overlay { position:fixed; inset:0; z-index:20; background:rgba(23,49,37,.5); display:flex; align-items:center; justify-content:center; padding:20px; }
.dialog { width:min(580px,100%); max-height:calc(100vh - 40px); overflow:auto; background:#fff; border-radius:14px; padding:28px; box-sizing:border-box; }
.dialog-heading { margin-bottom:24px; }.dialog-heading h2 { margin:0; }
label { display:block; font-size:14px; margin:18px 0; }
input,select,textarea { display:block; box-sizing:border-box; width:100%; font:inherit; margin-top:9px; padding:11px 13px; border:1px solid #cbdccb; border-radius:8px; color:#254a3d; background:#fff; }
textarea { resize:vertical; }.submit { width:100%; margin-top:10px; }
@media (max-width:700px) { .topbar,.heading,.section-heading { align-items:flex-start; flex-direction:column; }.record-list { grid-template-columns:1fr; }.batch-summary { padding:22px; } }
</style>

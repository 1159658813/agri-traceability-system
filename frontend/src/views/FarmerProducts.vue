<template>
  <div class="product-page">
    <header class="topbar">
      <div class="brand">
        <router-link to="/farmer">田野档案</router-link>
        <span>农户工作台 / 农产品档案</span>
      </div>
      <div class="account">
        <span v-if="account">Hi，{{ account.displayName || account.username }}</span>
        <router-link to="/farmer">返回工作台</router-link>
      </div>
    </header>

    <main class="main">
      <div class="page-heading">
        <div>
          <p class="eyebrow">农户档案管理</p>
          <h1>我的农产品</h1>
          <p>创建产品档案后，可在后续溯源批次中选择产品与种植基地。</p>
        </div>
        <button class="primary" type="button" :disabled="loading || categories.length === 0" @click="openCreate">新增产品</button>
      </div>

      <p v-if="error" class="alert" role="alert">{{ error }}</p>
      <p v-if="!categories.length && !loading" class="tip">当前尚无可用农产品分类，请联系管理员先配置产品分类。</p>
      <div v-if="loading" class="empty">正在加载产品档案...</div>
      <div v-else-if="!page.records.length" class="empty">暂无农产品档案，点击“新增产品”创建第一条记录</div>
      <div v-else class="product-grid">
        <article v-for="product in page.records" :key="product.id" class="product-card">
          <div class="card-heading">
            <div>
              <span class="category">{{ product.categoryName }}</span>
              <h2>{{ product.productName }}</h2>
            </div>
            <span :class="['state', product.status === 1 ? 'enabled' : 'disabled']">{{ product.status === 1 ? '使用中' : '已停用' }}</span>
          </div>
          <p class="detail">品种：{{ product.variety || '未填写' }}</p>
          <p v-if="product.description" class="description">{{ product.description }}</p>
          <p class="date">创建时间：{{ dateText(product.createdAt) }}</p>
          <div class="actions">
            <button type="button" :disabled="product.status !== 1" @click="openEdit(product)">编辑档案</button>
            <button type="button" @click="switchStatus(product)">{{ product.status === 1 ? '停用产品' : '恢复使用' }}</button>
          </div>
        </article>
      </div>

      <nav v-if="page.total > 0" class="pager" aria-label="产品分页">
        <span>共 {{ page.total }} 条 · 第 {{ page.pageNum }} 页</span>
        <button type="button" :disabled="page.pageNum <= 1 || loading" @click="load(page.pageNum - 1)">上一页</button>
        <button type="button" :disabled="page.pageNum * page.pageSize >= page.total || loading" @click="load(page.pageNum + 1)">下一页</button>
      </nav>
      <p class="note">停用不删除历史记录；有关联草稿或待审核批次的产品暂不能停用。产品与基地将在创建溯源批次时关联。</p>
    </main>

    <div v-if="editing" class="overlay" @click.self="closeForm">
      <div class="dialog" role="dialog" aria-modal="true" aria-label="农产品档案">
        <div class="dialog-heading">
          <h2>{{ editId ? '编辑产品' : '新增产品' }}</h2>
          <button type="button" @click="closeForm">关闭</button>
        </div>
        <form @submit.prevent="save">
          <label>农产品分类
            <select v-model.number="form.categoryId" required>
              <option :value="null" disabled>请选择产品分类</option>
              <option v-for="category in selectableCategories" :key="category.id" :value="category.id">{{ category.categoryName }}</option>
            </select>
          </label>
          <label>产品名称
            <input v-model.trim="form.productName" maxlength="100" required placeholder="例如：【演示】章丘草莓" />
          </label>
          <label>品种
            <input v-model.trim="form.variety" maxlength="100" placeholder="选填，例如：红颜" />
          </label>
          <label>产品介绍
            <textarea v-model.trim="form.description" maxlength="2000" rows="5" placeholder="选填：介绍品种特点、种植方向等，不填写未核实的质量结论" />
          </label>
          <p v-if="formError" class="alert" role="alert">{{ formError }}</p>
          <button class="primary submit" type="submit" :disabled="saving">{{ saving ? '保存中...' : '保存产品' }}</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { currentUser } from '../api/auth';
import {
  listProductCategories, listFarmerProducts,
  createFarmerProduct, updateFarmerProduct, setFarmerProductStatus
} from '../api/farmerProducts';

const blank = () => ({ categoryId: null, productName: '', variety: '', description: '' });

export default {
  name: 'FarmerProducts',
  data() {
    return {
      account: null, categories: [], loading: true, saving: false,
      editing: false, editId: null, form: blank(), originalCategory: null,
      formError: '', error: '',
      page: { total: 0, pageNum: 1, pageSize: 10, records: [] }
    };
  },
  computed: {
    selectableCategories() {
      if (!this.editId || !this.originalCategory) return this.categories;
      if (this.categories.some(category => category.id === this.originalCategory.id)) return this.categories;
      return [...this.categories, { ...this.originalCategory,
        categoryName: `${this.originalCategory.categoryName}（原分类已停用）` }];
    }
  },
  mounted() { this.start(); },
  methods: {
    dateText(value) { return value ? String(value).replace('T', ' ').slice(0, 16) : '—'; },
    async start() {
      try {
        this.account = await currentUser();
        if (this.account.roleCode !== 'FARMER') {
          await this.$router.replace('/manage');
          return;
        }
        this.categories = await listProductCategories();
        await this.load(1);
      } catch (error) {
        this.error = error.message || '产品档案加载失败';
        this.loading = false;
      }
    },
    async load(pageNum = 1) {
      this.loading = true;
      this.error = '';
      try { this.page = await listFarmerProducts(pageNum, 10); }
      catch (error) { this.error = error.message || '读取产品列表失败'; }
      finally { this.loading = false; }
    },
    openCreate() {
      this.editId = null;
      this.originalCategory = null;
      this.form = blank();
      this.formError = '';
      this.editing = true;
    },
    openEdit(product) {
      this.editId = product.id;
      this.originalCategory = { id: product.categoryId, categoryName: product.categoryName };
      this.form = {
        categoryId: product.categoryId, productName: product.productName,
        variety: product.variety || '', description: product.description || ''
      };
      this.formError = '';
      this.editing = true;
    },
    closeForm() { if (!this.saving) this.editing = false; },
    async save() {
      if (this.saving) return;
      this.formError = '';
      this.saving = true;
      try {
        if (!Number.isSafeInteger(this.form.categoryId) || this.form.categoryId <= 0) {
          throw new Error('请选择产品分类');
        }
        const payload = {
          categoryId: this.form.categoryId,
          productName: this.form.productName.trim(),
          variety: this.form.variety.trim() || null,
          description: this.form.description.trim() || null
        };
        if (!payload.productName) throw new Error('请填写产品名称');
        if (this.editId) await updateFarmerProduct(this.editId, payload);
        else await createFarmerProduct(payload);
        this.editing = false;
        await this.load(this.editId ? this.page.pageNum : 1);
      } catch (error) {
        this.formError = error.message || '产品保存失败';
      } finally {
        this.saving = false;
      }
    },
    async switchStatus(product) {
      const next = product.status === 1 ? 0 : 1;
      const action = next === 0 ? '停用' : '恢复';
      if (!window.confirm(`确认${action}“${product.productName}”？`)) return;
      this.error = '';
      try {
        await setFarmerProductStatus(product.id, next);
        await this.load(this.page.pageNum);
      } catch (error) {
        this.error = error.message || '状态更新失败';
      }
    }
  }
};
</script>

<style scoped>
.product-page { background:#f7f8f4; min-height:100vh; color:#234a3c; }
.topbar { display:flex; justify-content:space-between; align-items:center; gap:16px; padding:19px 5%; background:#fff; border-bottom:1px solid #e1e9df; }
.brand,.account { display:flex; align-items:center; gap:20px; font-size:14px; }
.brand a { color:#234a3c; font-size:19px; font-weight:700; text-decoration:none; }
.brand span { color:#819482; }.account a { color:#447356; text-decoration:none; }
.main { max-width:1140px; padding:38px 24px; margin:auto; }
.page-heading,.card-heading,.dialog-heading { display:flex; justify-content:space-between; align-items:center; gap:18px; }
.eyebrow { margin:0 0 8px; font-size:13px; color:#879e8c; }
.page-heading h1 { margin:0 0 9px; font-size:29px; }
.page-heading p:not(.eyebrow) { color:#79907f; font-size:14px; margin:0; }
button { background:white; color:#315943; font:inherit; border:1px solid #d2e0d1; border-radius:9px; padding:10px 15px; cursor:pointer; }
button:disabled { opacity:.6; cursor:not-allowed; }.primary { background:#234a3c; border-color:#234a3c; color:#fff; font-weight:600; }
.product-grid { margin-top:24px; display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:16px; }
.product-card { background:white; border:1px solid #e0e9df; padding:23px; border-radius:13px; min-width:0; }
.card-heading { align-items:start; }.card-heading h2 { font-size:20px; margin:10px 0 16px; overflow-wrap:anywhere; }
.category { color:#6b835f; font-size:12px; }.state { white-space:nowrap; border-radius:20px; padding:6px 11px; font-size:12px; }
.enabled { color:#32694c; background:#e7f1e7; }.disabled { color:#987e63; background:#f7efe3; }
.detail,.date { font-size:13px; color:#78907e; }.date { margin-top:16px; }
.description { font-size:14px; line-height:1.7; color:#496956; white-space:pre-wrap; overflow-wrap:anywhere; }
.actions { display:flex; gap:10px; margin-top:20px; }.actions button { font-size:13px; }
.empty,.tip { background:white; border:1px solid #e0e9df; border-radius:12px; padding:48px 20px; margin-top:20px; text-align:center; color:#819583; }
.tip { padding:14px; background:#f6f1df; border-color:#ebe2c3; color:#876b42; }
.alert { color:#a13c34; background:#fff0ee; padding:12px; border-radius:8px; }
.pager { display:flex; justify-content:end; align-items:center; gap:12px; margin-top:24px; font-size:13px; }
.note { margin:28px 0 0; color:#849685; font-size:13px; line-height:1.7; }
.overlay { position:fixed; inset:0; z-index:100; display:flex; justify-content:center; align-items:center; background:#1b2b25a8; padding:16px; overflow:auto; }
.dialog { background:white; width:min(650px,100%); max-height:94vh; overflow:auto; padding:28px; border-radius:16px; box-sizing:border-box; }
.dialog-heading { margin-bottom:22px; }.dialog-heading h2 { margin:0; font-size:25px; }
form { display:grid; gap:17px; }label { display:grid; gap:8px; font-size:14px; }
input,select,textarea { box-sizing:border-box; width:100%; border:1px solid #d0ded0; padding:12px 13px; border-radius:9px; background:#fff; font:inherit; color:#234a3c; }
textarea { resize:vertical; }.submit { padding:14px; }
@media (max-width:720px) { .topbar,.brand,.account { flex-wrap:wrap; }.main { padding:24px 15px; }.page-heading { align-items:start; flex-direction:column; }.product-grid { grid-template-columns:1fr; }.dialog { padding:20px; }.pager { justify-content:center; } }
</style>

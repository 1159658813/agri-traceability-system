<template>
  <div class="farmer-base-page">
    <header class="bar">
      <div class="brand"><router-link to="/farmer">田野档案</router-link><span>农户工作台 / 种植基地</span></div>
      <div class="account"><span>Hi，{{ account ? (account.displayName || account.username) : '' }}</span>
        <router-link to="/farmer">返回工作台</router-link>
      </div>
    </header>
    <main class="container">
      <div class="heading">
        <div><p class="eyebrow">农户档案管理</p><h1>我的种植基地</h1><p>只能查看和管理当前登录账号名下的基地</p></div>
        <button class="primary" type="button" @click="openCreate">新增基地</button>
      </div>
      <p v-if="error" class="alert" role="alert">{{ error }}</p>
      <div v-if="loading" class="empty">正在加载基地数据...</div>
      <div v-else-if="!page.records.length" class="empty">暂无种植基地，点击“新增基地”建立第一条档案</div>
      <div v-else class="cards">
        <article class="base-card" v-for="base in page.records" :key="base.id">
          <div class="card-head">
            <div><h2>{{ base.baseName }}</h2><p>{{ [base.province, base.city, base.district].filter(Boolean).join(' · ') }}</p></div>
            <span :class="['tag', base.status === 1 ? 'active' : 'inactive']">{{ base.status === 1 ? '使用中' : '已停用' }}</span>
          </div>
          <p class="address">地址：{{ base.address }}</p>
          <p class="detail">面积：{{ base.areaMu == null ? '未填写' : base.areaMu + ' 亩' }}</p>
          <p v-if="base.description" class="detail">{{ base.description }}</p>
          <div class="actions">
            <button type="button" @click="openEdit(base)">编辑</button>
            <button type="button" @click="switchStatus(base)">{{ base.status === 1 ? '停用基地' : '恢复使用' }}</button>
          </div>
        </article>
      </div>
      <div class="pager" v-if="page.total > 0">
        <span>共 {{ page.total }} 条 · 第 {{ page.pageNum }} 页</span>
        <button type="button" :disabled="page.pageNum <= 1 || loading" @click="load(page.pageNum - 1)">上一页</button>
        <button type="button" :disabled="page.pageNum * page.pageSize >= page.total || loading" @click="load(page.pageNum + 1)">下一页</button>
      </div>
      <p class="note">停用不会删除基地及已有溯源记录；关联草稿或待审核批次的基地暂不能停用。</p>
    </main>

    <div v-if="editing" class="overlay" @click.self="closeForm">
      <div class="dialog" role="dialog" aria-modal="true" aria-label="基地信息">
        <div class="dialog-heading"><h2>{{ editId ? '编辑基地' : '新增基地' }}</h2><button type="button" @click="closeForm">关闭</button></div>
        <form @submit.prevent="save">
          <label>基地名称 <input v-model.trim="form.baseName" maxlength="100" required /></label>
          <div class="two">
            <label>省份 <input v-model.trim="form.province" maxlength="50" required /></label>
            <label>城市 <input v-model.trim="form.city" maxlength="50" required /></label>
          </div>
          <label>区县 <input v-model.trim="form.district" maxlength="50" /></label>
          <label>详细地址 <input v-model.trim="form.address" maxlength="255" required /></label>
          <label>种植面积（亩）<input v-model="form.areaMu" type="number" min="0" max="99999999.99" step="0.01" placeholder="选填" /></label>
          <label>基地介绍 <textarea v-model.trim="form.description" rows="3" maxlength="500" /></label>
          <p v-if="formError" class="alert" role="alert">{{ formError }}</p>
          <button class="primary" type="submit" :disabled="saving">{{ saving ? '正在保存...' : '保存基地' }}</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { currentUser } from '../api/auth';
import { listFarmerBases, createFarmerBase, updateFarmerBase, setFarmerBaseStatus } from '../api/farmerBases';

const blank = () => ({ baseName: '', province: '', city: '', district: '', address: '', areaMu: '', description: '' });

export default {
  name: 'FarmerBases',
  data() {
    return { account: null, loading: true, saving: false, editing: false, editId: null,
      form: blank(), formError: '', error: '', page: { total: 0, pageNum: 1, pageSize: 10, records: [] } };
  },
  mounted() { this.start(); },
  methods: {
    async start() {
      try {
        this.account = await currentUser();
        if (this.account.roleCode !== 'FARMER') { this.$router.replace('/manage'); return; }
        await this.load(1);
      } catch (error) {
        this.error = error.message || '无法读取登录信息';
        this.loading = false;
      }
    },
    async load(pageNum = 1) {
      this.loading = true; this.error = '';
      try { this.page = await listFarmerBases(pageNum, 10); }
      catch (error) { this.error = error.message || '基地列表读取失败'; }
      finally { this.loading = false; }
    },
    openCreate() { this.editId = null; this.form = blank(); this.formError = ''; this.editing = true; },
    openEdit(base) {
      this.editId = base.id;
      this.form = { baseName: base.baseName, province: base.province, city: base.city,
        district: base.district || '', address: base.address,
        areaMu: base.areaMu == null ? '' : String(base.areaMu), description: base.description || '' };
      this.formError = ''; this.editing = true;
    },
    closeForm() { if (!this.saving) this.editing = false; },
    async save() {
      if (this.saving) return;
      this.formError = ''; this.saving = true;
      try {
        const form = { ...this.form, areaMu: this.form.areaMu === '' ? null : Number(this.form.areaMu) };
        if (form.areaMu !== null && (!Number.isFinite(form.areaMu) || form.areaMu < 0)) {
          throw new Error('种植面积不正确');
        }
        if (this.editId) await updateFarmerBase(this.editId, form);
        else await createFarmerBase(form);
        this.editing = false;
        await this.load(1);
      } catch (error) { this.formError = error.message || '保存失败'; }
      finally { this.saving = false; }
    },
    async switchStatus(base) {
      const next = base.status === 1 ? 0 : 1;
      const action = next === 0 ? '停用' : '恢复使用';
      if (!window.confirm(`确认${action}“${base.baseName}”？`)) return;
      this.error = '';
      try { await setFarmerBaseStatus(base.id, next); await this.load(this.page.pageNum); }
      catch (error) { this.error = error.message || '操作失败'; }
    }
  }
};
</script>

<style scoped>
.farmer-base-page { min-height: 100vh; background: #f7f8f4; color: #234a3c; }
.bar { display: flex; justify-content: space-between; align-items: center; gap: 15px; padding: 20px 5%; background: white; border-bottom: 1px solid #e0e8df; }
.brand, .account { display: flex; gap: 20px; align-items: center; font-size: 14px; }.brand a { font-weight: 700; font-size: 19px; color: #234a3c; text-decoration: none; }.brand span { color: #819583; }
.account a { color: #457554; text-decoration: none; }.container { max-width: 1150px; margin: auto; padding: 38px 24px; }
.heading, .card-head, .dialog-heading { display: flex; justify-content: space-between; align-items: center; gap: 18px; }.heading h1 { margin: 5px 0; font-size: 28px; }.heading p { color: #7c947e; font-size: 13px; }.eyebrow { font-size: 13px; color: #8a9f8e; }
button { cursor: pointer; font: inherit; border-radius: 8px; border: 1px solid #cbdcca; background: white; padding: 10px 15px; color: #315a43; }button:disabled { opacity: .6; cursor: not-allowed; }.primary { background: #234a3c; border-color: #234a3c; color: #fff; font-weight: 600; }
.cards { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; margin-top: 24px; }.base-card { padding: 23px; background: #fff; border: 1px solid #e2e9de; border-radius: 12px; }.card-head h2 { font-size: 20px; margin: 0 0 8px; }.card-head p, .detail { color: #758d78; font-size: 13px; }.address { font-size: 14px; }.tag { white-space: nowrap; border-radius: 20px; padding: 6px 10px; font-size: 12px; }.active { color: #326347; background: #e5f2e5; }.inactive { color: #916c3d; background: #f6f0e5; }.actions { display: flex; gap: 10px; margin-top: 18px; }.actions button { font-size: 13px; padding: 8px 12px; }
.pager { display: flex; align-items: center; justify-content: end; gap: 10px; margin-top: 22px; }.note { font-size: 13px; color: #8a9e8d; margin-top: 20px; }.empty { background: #fff; border: 1px solid #e2e9de; border-radius: 12px; text-align: center; padding: 55px 20px; margin-top: 24px; }.alert { background: #fff0ed; color: #954c45; padding: 12px; border-radius: 8px; margin-top: 18px; }
.overlay { position: fixed; z-index: 20; inset: 0; background: rgba(23, 48, 32, .45); display: grid; place-items: center; padding: 16px; }.dialog { background: #fff; padding: 24px; width: 100%; max-width: 600px; border-radius: 13px; max-height: 90vh; overflow: auto; box-sizing: border-box; }.dialog-heading h2 { margin: 0; }.dialog form { display: grid; gap: 14px; margin-top: 24px; }.dialog label { display: grid; gap: 8px; font-size: 14px; }.dialog input, .dialog textarea { padding: 11px; font: inherit; color: #234a3c; border: 1px solid #d3ded3; border-radius: 8px; width: 100%; box-sizing: border-box; }.two { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
@media(max-width:700px) { .bar,.heading { flex-wrap:wrap; }.cards,.two { grid-template-columns: 1fr; }.container { padding: 24px 14px; } }
</style>

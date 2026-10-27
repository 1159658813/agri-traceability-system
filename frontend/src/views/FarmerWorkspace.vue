<template>
  <div class="farmer-shell">
    <aside class="sidebar">
      <router-link class="brand" to="/">
        <span class="brand-mark">田</span>
        <span><strong>田野档案</strong><small>农户工作台</small></span>
      </router-link>
      <p class="group-title">工作空间</p>
      <router-link class="nav-current" to="/farmer">工作台概览</router-link>
      <p class="group-title">溯源业务</p>
      <span class="nav-disabled">种植基地 <small>后续开放</small></span>
      <span class="nav-disabled">农产品档案 <small>后续开放</small></span>
      <span class="nav-disabled">溯源批次 <small>后续开放</small></span>
      <span class="nav-disabled">农事与采收 <small>后续开放</small></span>
    </aside>

    <div class="workspace">
      <header class="topbar">
        <strong>农户工作台</strong>
        <div class="account">
          <span v-if="account">Hi，{{ account.displayName || account.username }}</span>
          <router-link class="home-link" to="/">公众首页</router-link>
          <button type="button" :disabled="loggingOut" @click="signOut">退出登录</button>
        </div>
      </header>
      <main class="content">
        <div class="intro">
          <span>我的种植档案</span>
          <h1>{{ account ? `${account.displayName || account.username}，欢迎回来` : '农户工作台' }}</h1>
          <p>下方只展示当前登录农户名下的数据，不包含其他农户的基地、产品或批次。</p>
        </div>
        <p v-if="error" class="error" role="alert">{{ error }}</p>
        <div v-if="loading" class="empty">正在读取个人业务数据...</div>
        <template v-else-if="overview">
          <div class="stat-grid">
            <div v-for="card in statCards" :key="card.label" class="stat-card">
              <span>{{ card.label }}</span>
              <strong>{{ card.value }}</strong>
            </div>
          </div>
          <section class="batches">
            <div class="section-heading">
              <div><h2>最近的溯源批次</h2><p>最多显示最近 10 条记录</p></div>
              <button type="button" :disabled="loading" @click="loadOverview">刷新数据</button>
            </div>
            <div v-if="!overview.recentBatches.length" class="empty">当前账号暂无可显示的溯源批次</div>
            <div v-else class="table-wrap">
              <table>
                <thead><tr><th>批次编号</th><th>产品</th><th>种植基地</th><th>状态</th><th>创建日期</th></tr></thead>
                <tbody>
                  <tr v-for="batch in overview.recentBatches" :key="batch.id">
                    <td>{{ batch.batchNo }}</td><td>{{ batch.productName }}</td>
                    <td>{{ batch.baseName }}</td><td><span class="status">{{ statusText(batch.status) }}</span></td>
                    <td>{{ dateText(batch.createdAt) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
          <p class="note">该页面目前为只读工作台；新增和修改操作将在归属权限校验完成后逐步开放。</p>
        </template>
      </main>
    </div>
  </div>
</template>

<script>
import { currentUser, logout } from '../api/auth';
import { getFarmerOverview } from '../api/farmerWorkspace';

export default {
  name: 'FarmerWorkspace',
  data() {
    return { account: null, overview: null, loading: true, loggingOut: false, error: '' };
  },
  computed: {
    statCards() {
      const stats = this.overview;
      if (!stats) return [];
      return [
        { label: '种植基地', value: stats.baseCount },
        { label: '农产品档案', value: stats.productCount },
        { label: '全部批次', value: stats.batchCount },
        { label: '已发布批次', value: stats.publishedCount },
        { label: '待审核批次', value: stats.pendingCount }
      ];
    }
  },
  mounted() { this.loadOverview(); },
  methods: {
    statusText(value) {
      return { DRAFT: '草稿', SUBMITTED: '待审核', PUBLISHED: '已发布', REJECTED: '已驳回' }[value] || value;
    },
    dateText(value) { return value ? String(value).replace('T', ' ').slice(0, 16) : '—'; },
    async loadOverview() {
      this.loading = true;
      this.error = '';
      try {
        this.account = await currentUser();
        if (this.account.roleCode !== 'FARMER') {
          await this.$router.replace('/manage');
          return;
        }
        this.overview = await getFarmerOverview();
      } catch (error) {
        this.error = error.message || '工作台加载失败';
      } finally {
        this.loading = false;
      }
    },
    async signOut() {
      if (this.loggingOut) return;
      this.loggingOut = true;
      this.error = '';
      try {
        const user = this.account || await currentUser();
        await logout(user.csrfToken);
        await this.$router.replace('/login');
      } catch (error) {
        this.error = error.message || '退出失败';
      } finally {
        this.loggingOut = false;
      }
    }
  }
};
</script>

<style scoped>
.farmer-shell { min-height: 100vh; display: flex; color: #234a3c; background: #f7f8f4; }
.sidebar { width: 220px; flex-shrink: 0; background: #f3f5ef; border-right: 1px solid #dfe7dd; padding: 23px 16px; box-sizing: border-box; }
.brand { display: flex; align-items: center; gap: 12px; color: #234a3c; text-decoration: none; margin-bottom: 48px; }
.brand-mark { display: grid; place-items: center; width: 42px; height: 42px; background: #234a3c; color: #fff; border-radius: 12px; }
.brand strong { display: block; font-size: 18px; }.brand small { display: block; color: #819683; font-size: 12px; margin-top: 3px; }
.group-title { font-size: 12px; font-weight: 600; color: #91a493; margin: 30px 12px 12px; }
.nav-current, .nav-disabled { display: flex; justify-content: space-between; align-items: center; padding: 13px 12px; border-radius: 9px; color: #3f5c47; text-decoration: none; font-size: 14px; }
.nav-current { color: #245641; background: #dce9dd; font-weight: 600; }
.nav-disabled { opacity: .65; cursor: default; }.nav-disabled small { font-size: 11px; }
.workspace { flex: 1; min-width: 0; }.topbar { display: flex; justify-content: space-between; align-items: center; padding: 18px 34px; background: #fff; border-bottom: 1px solid #e4e9e1; }
.account { display: flex; align-items: center; gap: 18px; font-size: 14px; }.account button, .section-heading button { border: 1px solid #d3e1d5; color: #315e47; background: #fff; border-radius: 8px; padding: 9px 14px; cursor: pointer; }
.home-link { color: #587762; text-decoration: none; }.content { max-width: 1300px; padding: 34px; margin: 0 auto; }
.intro { background: #234a3c; border-radius: 15px; padding: 34px 38px; color: #fff; }.intro span { font-size: 13px; color: #c5d9c5; }
.intro h1 { font-size: 31px; margin: 12px 0; }.intro p { font-size: 14px; color: #e1ebe0; }
.stat-grid { display: grid; grid-template-columns: repeat(5,minmax(0,1fr)); gap: 14px; margin-top: 22px; }
.stat-card { background: #fff; border: 1px solid #e1e9e0; border-radius: 12px; padding: 22px; }
.stat-card span { font-size: 13px; color: #7a907f; }.stat-card strong { display: block; font-size: 32px; margin-top: 11px; }
.batches { margin-top: 25px; border: 1px solid #e1e9e0; border-radius: 13px; background: #fff; padding: 22px; }
.section-heading { display: flex; align-items: center; justify-content: space-between; }.section-heading h2 { font-size: 19px; margin: 0 0 8px; }.section-heading p { font-size: 12px; color: #839686; margin: 0 0 18px; }
.table-wrap { overflow-x: auto; }table { width: 100%; border-collapse: collapse; font-size: 13px; text-align: left; }th,td { padding: 14px 12px; border-bottom: 1px solid #edf2ec; white-space: nowrap; }th { color: #788e7d; font-weight: 500; }
.status { background: #e8f1e7; color: #386a4a; padding: 5px 10px; border-radius: 99px; }.empty { padding: 44px 16px; text-align: center; color: #809583; }
.error { color: #9d443a; background: #fff0ee; border-radius: 8px; padding: 12px; }.note { font-size: 13px; color: #809583; margin-top: 18px; }
@media (max-width: 1100px) { .stat-grid { grid-template-columns: repeat(3,minmax(0,1fr)); } }
@media (max-width: 760px) { .farmer-shell { display: block; }.sidebar { width: auto; padding: 16px; }.brand { margin-bottom: 10px; }.group-title, .nav-disabled { display: none; }.topbar { padding: 16px; flex-wrap: wrap; gap: 12px; }.account { flex-wrap: wrap; gap: 10px; }.content { padding: 18px; }.stat-grid { grid-template-columns: repeat(2,minmax(0,1fr)); }.intro { padding: 25px 22px; }.intro h1 { font-size: 23px; } }
</style>

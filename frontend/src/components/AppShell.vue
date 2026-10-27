<template>
  <div class="shell">
    <div v-if="menuOpen" class="mobile-backdrop" @click="menuOpen = false"></div>
    <aside class="sidebar" :class="{ 'sidebar-open': menuOpen }">
      <div class="brand" @click="go('/manage')">
        <div class="brand-stamp">
          <svg viewBox="0 0 44 44" aria-hidden="true"><path d="M22 36V17M22 25c-10 0-14-7-14-14 10 0 14 7 14 14ZM22 20c1-10 7-13 15-12 0 9-5 15-15 15" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"/><path d="M11 36h22" stroke="currentColor" stroke-width="2.6" stroke-linecap="round"/></svg>
        </div>
        <div class="brand-wordmark"><strong>田野档案</strong><span>农产品产地溯源管理</span></div>
      </div>
      <div class="sidebar-separator"></div>
      <div class="nav-scroll">
        <div class="nav-title">工作空间</div>
        <button class="nav-link" :class="{ active: $route.name === 'dashboard' }" @click="go('/manage')"><i class="el-icon-menu"></i> <span>概览工作台</span><b v-if="$route.name === 'dashboard'" class="nav-current"></b></button>
        <button class="nav-link" :class="{ active: $route.name === 'bases' }" @click="go('/manage/bases')"><i class="el-icon-map-location"></i> <span>种植基地</span><b v-if="$route.name === 'bases'" class="nav-current"></b></button>
        <div class="nav-title nav-margin">溯源业务</div>
        <button v-for="item in modules" :key="item.key" class="nav-link" :class="{ active: $route.params.section === item.key }" @click="go('/manage/module/' + item.key)"><i :class="item.icon"></i><span>{{ item.title }}</span><b class="nav-locked">·</b></button>
      </div>
      <div class="sidebar-bottom">
        <div class="sidebar-note"><div class="note-mark">V1</div><strong>溯源，从产地开始</strong><p>基础业务管理版 · 持续建设中</p></div>
        <div class="sidebar-footer"><span class="footer-dot"></span> 本地开发环境 <span>2026</span></div>
      </div>
    </aside>

    <div class="shell-main">
      <header class="topbar">
        <div class="top-left">
          <button class="mobile-menu" aria-label="打开导航" @click="menuOpen = true"><i class="el-icon-s-unfold"></i></button>
          <div class="section-marker">产地溯源管理</div>
          <div class="top-divider"></div>
          <div class="top-crumb">{{ currentTitle }}</div>
        </div>
        <div class="top-right"><button class="back-to-site" @click="go('/')">返回公众首页</button><span class="environment"><span class="environment-dot"></span> <AccountGreeting />  本地开发</span><span class="top-avatar">农</span></div>
      </header>
      <main class="page-viewport"><router-view /></main>
      <footer class="global-footer"><span>田野档案 · 农产品产地溯源信息管理系统</span><span>已接入数据来自本地后端</span></footer>
    </div>
  </div>
</template>
<script>
import AccountGreeting from './AccountGreeting.vue';
const MODULES = [
  { key: 'products', title: '农产品档案', icon: 'el-icon-collection' },
  { key: 'batches', title: '溯源批次', icon: 'el-icon-tickets' },
  { key: 'farm', title: '农事记录', icon: 'el-icon-notebook-2' },
  { key: 'harvest', title: '采收管理', icon: 'el-icon-s-management' },
  { key: 'quality', title: '质量检测', icon: 'el-icon-circle-check' },
  { key: 'logistics', title: '流通记录', icon: 'el-icon-truck' },
  { key: 'completeness', title: '完整性评价', icon: 'el-icon-data-analysis' }
];
export default {
  name: 'AppShell',
  data() { return { menuOpen: false, modules: MODULES }; },
  computed: {
    currentTitle() {
      if (this.$route.name === 'dashboard') return '总览工作台';
      if (this.$route.name === 'bases') return '种植基地';
      const item = MODULES.find(x => x.key === this.$route.params.section);
      return item ? item.title : '工作空间';
    }
  },
  watch: { '$route.path'() { this.menuOpen = false; } },
  methods: {
    go(path) { if (this.$route.path !== path) this.$router.push(path); this.menuOpen = false; }
  },
  components: {
    AccountGreeting
  }
};
</script>
<style scoped>
.shell{display:flex;min-height:100vh}.sidebar{width:251px;flex:0 0 251px;background:#f5f6f0;border-right:1px solid #e0e5dc;display:flex;flex-direction:column;position:fixed;inset:0 auto 0 0;z-index:21}.brand{height:111px;display:flex;align-items:center;padding:19px 26px 16px;gap:13px;cursor:pointer}.brand-stamp{width:47px;height:47px;border-radius:15px 15px 15px 5px;background:#274a3b;color:#d8e4d0;display:flex;align-items:center;justify-content:center;box-shadow:0 7px 14px rgba(35,74,60,.1)}.brand-stamp svg{width:34px;height:34px}.brand-wordmark{display:flex;flex-direction:column;gap:2px}.brand-wordmark strong{font-size:21px;letter-spacing:2.1px;color:#254a3c;font-weight:750}.brand-wordmark span{color:#9aab9d;font-size:9px;letter-spacing:2.2px;font-weight:700}.sidebar-separator{height:1px;background:#e2e7dd;margin:0 26px 19px}.nav-scroll{padding:0 15px;overflow-y:auto;flex:1}.nav-title{font-size:11px;color:#9ba99b;letter-spacing:1.6px;font-weight:700;display:flex;justify-content:space-between;margin:10px 12px 11px}.nav-title span{color:#b6c1b3;letter-spacing:1px}.nav-margin{margin-top:30px}.nav-link{font:inherit;border:0;width:100%;height:45px;padding:0 14px;display:flex;align-items:center;text-align:left;gap:12px;color:#65756b;background:transparent;border-radius:9px;cursor:pointer;margin:2px 0;position:relative;font-size:13px;transition:background .15s,color .15s}.nav-link i{font-size:17px;width:21px;text-align:center}.nav-link span{flex:1}.nav-link:hover{background:#e9eee6;color:#274e3d}.nav-link.active{background:#dce9dd;color:#1c5035;font-weight:650}.nav-current{width:5px;height:15px;border-radius:4px;background:#3b8060}.nav-locked{font-size:21px;color:#bccbbf;font-weight:400}.sidebar-bottom{padding:16px}.sidebar-note{background:#e4eadf;padding:19px 19px 17px;border-radius:13px;position:relative;overflow:hidden}.sidebar-note:after{content:'';width:105px;height:105px;border:1px solid rgba(68,117,78,.13);border-radius:100%;position:absolute;right:-35px;top:-29px}.note-mark{color:#568466;font-family:Georgia,serif;font-size:21px;margin-bottom:12px}.note-mark span{font-family:inherit;font-size:11px}.sidebar-note strong{display:block;color:#294a37;font-size:13px}.sidebar-note p{color:#859788;font-size:11px;margin:7px 0 0}.sidebar-footer{display:flex;align-items:center;font-size:10px;color:#9dad9d;margin:19px 8px 2px;gap:7px}.sidebar-footer span:last-child{margin-left:auto}.footer-dot{background:#79a68a;width:6px;height:6px;border-radius:100%}.shell-main{flex:1;min-width:0;margin-left:251px;display:flex;flex-direction:column;min-height:100vh}.topbar{height:77px;background:#fff;flex:0 0 77px;border-bottom:1px solid #e8ece5;display:flex;align-items:center;justify-content:space-between;padding:0 39px}.top-left,.top-right{display:flex;align-items:center}.section-marker{font-size:10px;font-weight:750;color:#8c9a8d;letter-spacing:1.75px}.section-marker span{margin:0 7px;color:#d1d9cf}.top-divider{height:19px;width:1px;background:#e5e9e3;margin:0 23px}.top-crumb{font-size:13px;color:#374c3e;font-weight:600}.top-right{gap:22px}.environment{font-size:10px;color:#668971;letter-spacing:1px;font-weight:650}.environment-dot{width:7px;height:7px;display:inline-block;background:#75a88d;border-radius:100%;margin-right:7px}.top-avatar{display:grid;place-items:center;width:34px;height:34px;background:#f0e8d9;color:#5f704b;border-radius:50%;font-size:14px;font-weight:800}.page-viewport{flex:1;padding:32px 39px 40px;max-width:1600px;width:100%;margin:0 auto}.global-footer{display:flex;justify-content:space-between;padding:0 39px 20px;font-size:11px;color:#9ca8a0}.mobile-menu{display:none;background:transparent;border:0;color:#2c503e;font-size:21px;margin-right:17px}.mobile-backdrop{display:none}@media(max-width:1080px){.sidebar{width:215px;flex-basis:215px}.shell-main{margin-left:215px}.brand{padding-left:18px}.topbar{padding:0 25px}.page-viewport{padding:25px}.global-footer{padding:0 25px 20px}}@media(max-width:790px){.sidebar{transform:translateX(-100%);transition:transform .2s;width:251px}.sidebar.sidebar-open{transform:translateX(0)}.mobile-backdrop{display:block;position:fixed;inset:0;background:rgba(0,0,0,.38);z-index:20}.shell-main{margin-left:0}.mobile-menu{display:block}.section-marker{display:none}.top-divider{display:none}.page-viewport{padding:20px 16px 28px}.topbar{height:66px;flex-basis:66px;padding:0 17px}.global-footer{padding:0 18px 20px;gap:12px}.global-footer span:last-child{display:none}.environment{display:none}}

.back-to-site{border:0;background:#e9f0e7;color:#3b6849;padding:9px 13px;border-radius:7px;cursor:pointer;font-size:13px}
/* V1.1 可读性调整：加大常用字号，保留现有页面布局 */

/* 导航区域 */
.sidebar { width: 267px; flex-basis: 267px; }
.shell-main { margin-left: 267px; }
.brand { padding-left: 24px; }
.brand-wordmark strong { font-size: 22px; }
.brand-wordmark span { font-size: 12px; letter-spacing: .4px; color: #748d78; }
.nav-title { font-size: 13px; letter-spacing: .4px; margin-top: 12px; margin-bottom: 12px; color: #718774; }
.nav-margin { margin-top: 25px; }
.nav-link { min-height: 49px; height: 49px; padding: 0 15px; font-size: 15px; gap: 13px; }
.nav-link i { font-size: 19px; width: 22px; }
.nav-locked { font-size: 20px; }
.sidebar-note strong { font-size: 15px; }
.sidebar-note p { font-size: 13px; line-height: 1.5; }
.sidebar-footer { font-size: 12px; }
/* 顶栏、底部 */
.section-marker { font-size: 14px; letter-spacing: .2px; font-weight: 650; }
.top-crumb { font-size: 15px; }
.environment { font-size: 13px; letter-spacing: 0; }
.global-footer { font-size: 12px; }
@media (max-width: 1080px) {
  .sidebar { width: 232px; flex-basis: 232px; }
  .shell-main { margin-left: 232px; }
}
@media (max-width: 790px) {
  .sidebar { width: 267px; flex-basis: 267px; }
  .shell-main { margin-left: 0; }
}

</style>

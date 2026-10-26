<template>
  <div class="public-site">
    <header class="site-header">
      <router-link to="/" class="site-brand" aria-label="田野档案首页">
        <span class="brand-symbol"><i class="el-icon-s-flag"></i></span>
        <span><strong>田野档案</strong><small>农产品产地溯源平台</small></span>
      </router-link>
      <nav :class="{ expanded: menuOpen }" class="site-nav" aria-label="网站导航">
        <router-link exact to="/">首页</router-link>
        <router-link to="/search">溯源查询</router-link>
        <router-link to="/notices">平台公告</router-link>
        <router-link to="/farmer-apply">农户入驻</router-link>
        <router-link to="/login" class="login-link">后台登录</router-link>
        <router-link v-if="isDev" to="/manage" class="dev-link">管理端预览</router-link>
      </nav>
      <button class="nav-toggle" type="button" :aria-expanded="String(menuOpen)" aria-label="展开导航" @click="menuOpen = !menuOpen"><i class="el-icon-s-unfold"></i></button>
    </header>
    <main class="site-main"><router-view /></main>
    <footer class="site-footer"><span>田野档案 · 农产品产地溯源信息管理系统</span><span>平台展示的统计仅包含已公开批次</span></footer>
  </div>
</template>
<script>
export default {
  name: 'PublicLayout',
  data() { return { menuOpen: false, isDev: process.env.NODE_ENV !== 'production' }; },
  watch: { '$route.fullPath'() { this.menuOpen = false; } }
};
</script>
<style scoped>
.public-site{min-height:100vh;display:flex;flex-direction:column;background:#f7f8f4}.site-header{position:relative;z-index:8;height:88px;padding:0 max(30px,calc((100vw - 1350px)/2));display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid #e5eae2;background:#fff}.site-brand{display:flex;align-items:center;gap:12px;text-decoration:none;color:#254838}.brand-symbol{width:46px;height:46px;background:#254838;color:#e5d9ae;border-radius:12px;display:grid;place-items:center;font-size:24px}.site-brand strong{font-size:23px;letter-spacing:1.3px;display:block}.site-brand small{font-size:12px;color:#718776;letter-spacing:.5px;display:block}.site-nav{display:flex;gap:27px;align-items:center}.site-nav a{font-size:15px;color:#536c5a;text-decoration:none;white-space:nowrap}.site-nav a.router-link-active:not(.dev-link){color:#24543a;font-weight:700}.site-nav .login-link{background:#2a5b41;border-radius:7px;padding:10px 17px;color:white}.site-nav .dev-link{color:#9a8d72;font-size:13px}.site-main{width:100%;max-width:1410px;margin:0 auto;flex:1;padding:36px 30px 64px}.site-footer{padding:20px max(30px,calc((100vw - 1350px)/2));border-top:1px solid #e5eae2;display:flex;justify-content:space-between;color:#8b9e90;font-size:13px;gap:15px}.nav-toggle{display:none;background:transparent;border:0;color:#315740;font-size:25px}@media(max-width:1000px){.site-header{padding:0 24px}.site-nav{gap:14px}.site-nav a{font-size:13px}.site-main{padding:26px 22px 50px}}@media(max-width:760px){.site-header{height:75px;padding:0 18px}.site-brand strong{font-size:19px}.site-brand small{font-size:11px}.nav-toggle{display:block}.site-nav{display:none;position:absolute;top:74px;left:0;right:0;background:#fff;padding:16px 22px 22px;box-shadow:0 12px 24px rgba(30,70,46,.12);flex-direction:column;align-items:stretch;gap:18px}.site-nav.expanded{display:flex}.site-main{padding:18px 14px 34px}.site-footer{padding:18px;flex-direction:column}}
</style>

<template>
  <div class="login-page">
    <div class="login-panel">
      <div class="login-label">田野档案 · 账号访问</div>
      <h1>账号登录</h1>
      <p class="login-description">管理员和审核通过的农户均可使用已启用的账号登录。</p>

      <form @submit.prevent="submitLogin">
        <label for="login-username">账号</label>
        <input id="login-username" v-model.trim="username" type="text"
               autocomplete="username" maxlength="50" placeholder="请输入账号" required />
        <label for="login-password">密码</label>
        <input id="login-password" v-model="password" type="password"
               autocomplete="current-password" maxlength="128" placeholder="请输入密码" required />
        <p v-if="error" class="login-error" role="alert">{{ error }}</p>
        <button type="submit" :disabled="submitting">
          {{ submitting ? '正在验证...' : '登录工作台' }}
        </button>
      </form>
      <router-link class="login-back" to="/">返回公众首页</router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginPage',
  data() {
    return { username: '', password: '', submitting: false, error: '' };
  },
  methods: {
    async submitLogin() {
      if (this.submitting) return;
      this.error = '';
      this.submitting = true;
      try {
        const response = await fetch('/api/auth/login', {
          method: 'POST',
          credentials: 'same-origin',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username: this.username, password: this.password })
        });
        const result = await response.json();
        if (!response.ok || result.code !== 200) {
          this.error = result.message || '登录失败，请检查账号密码';
          return;
        }
        this.password = '';
        const role = result.data.roleCode;
        const home = role === 'ADMIN' ? '/manage' : role === 'FARMER' ? '/farmer' : null;
        if (!home) {
          this.error = '当前账号角色未开放';
          return;
        }
        const redirect = this.$route.query.redirect;
        const allowed = typeof redirect === 'string' &&
          (redirect === home || redirect.startsWith(`${home}/`));
        await this.$router.replace(allowed ? redirect : home);
      } catch (error) {
        this.error = '暂时无法连接服务器，请检查后端是否启动';
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>

<style scoped>
.login-page { min-height: calc(100vh - 160px); padding: 64px 20px;
  background: #f7f8f4; display: flex; justify-content: center; align-items: flex-start; }
.login-panel { width: 100%; max-width: 420px; background: #fff; border: 1px solid #e1e8de;
  border-radius: 15px; padding: 38px 36px; box-shadow: 0 16px 45px rgba(23, 60, 41, .06); }
.login-label { color: #7e977f; font-size: 13px; }
h1 { margin: 10px 0 4px; color: #234a3c; font-size: 28px; }
.login-description { color: #728276; font-size: 13px; line-height: 1.7; margin-bottom: 26px; }
form { display: flex; flex-direction: column; }
label { color: #385345; font-size: 14px; margin: 0 0 9px; }
input { border: 1px solid #dce6dd; border-radius: 8px; background: #fff; color: #263d30;
  font-size: 15px; padding: 12px; margin-bottom: 20px; outline: none; width: 100%; box-sizing: border-box; }
input:focus { border-color: #447a61; box-shadow: 0 0 0 3px rgba(68, 122, 97, .1); }
button { background: #234a3c; color: #fff; border: none; border-radius: 8px;
  padding: 13px; font-weight: 600; cursor: pointer; font-size: 15px; }
button:disabled { opacity: .65; cursor: not-allowed; }
.login-error { color: #a94442; background: #fff2f0; padding: 10px;
  border-radius: 7px; font-size: 13px; }
.login-back { color: #54775d; display: inline-block; margin-top: 21px;
  text-decoration: none; font-size: 13px; }
@media(max-width: 520px) { .login-page { padding: 25px 14px; }
  .login-panel { padding: 28px 22px; } }
</style>

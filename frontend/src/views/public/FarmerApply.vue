<template>
  <main class="apply-page">
    <section class="apply-panel">
      <p class="eyebrow">田野档案 · 农户入驻</p>
      <h1>申请入驻</h1>
      <p class="subtitle">提交经营信息与证明材料，管理员审核通过后才能启用农户账号。</p>
      <p class="notice">当前为本地开发测试环境。请使用测试账号和自建示例资料，不要上传身份证、真实营业执照等个人敏感材料。</p>
      <form v-if="!submitted" @submit.prevent="submit">
        <div class="two-col">
          <label>登录账号 <input v-model.trim="form.username" required minlength="4" maxlength="50" pattern="[a-zA-Z][a-zA-Z0-9_]{3,49}" autocomplete="username" placeholder="英文开头，4～50位" /></label>
          <label>登录密码 <input v-model="form.password" required type="password" minlength="6" maxlength="128" autocomplete="new-password" placeholder="6～128位" /></label>
          <label>主体类型
            <select v-model="form.applicantType"><option value="INDIVIDUAL">个人农户</option><option value="COOPERATIVE">合作社</option></select>
          </label>
          <label v-if="form.applicantType === 'COOPERATIVE'">合作社名称 <input v-model.trim="form.organizationName" required maxlength="120" /></label>
          <label>联系人 <input v-model.trim="form.contactName" required maxlength="80" /></label>
          <label>联系电话 <input v-model.trim="form.contactPhone" required maxlength="30" placeholder="用于审核联系" /></label>
        </div>
        <label>生产经营地区 <input v-model.trim="form.farmingLocation" required maxlength="255" placeholder="例如：四川省德阳市某种植基地（演示）" /></label>
        <label>资质材料类型
          <select v-model="form.proofType">
            <option value="LAND_CONTRACT">土地经营相关材料</option>
            <option value="BUSINESS_LICENSE">主体经营证明</option>
            <option value="OTHER_PROOF">其他生产经营证明</option>
          </select>
        </label>
        <label>证明材料（PDF/JPG/PNG，最大5MB）
          <input type="file" accept=".pdf,.png,.jpg,.jpeg,application/pdf,image/png,image/jpeg" required @change="onFile" />
        </label>
        <label>申请说明 <textarea v-model.trim="form.applicationNote" maxlength="500" rows="3" placeholder="可简单介绍种植产品、基地情况"></textarea></label>
        <p v-if="error" class="error" role="alert">{{ error }}</p>
        <button type="submit" :disabled="submitting">{{ submitting ? '正在提交...' : '提交入驻申请' }}</button>
      </form>
      <div v-else class="success">
        <h2>申请已提交</h2>
        <p>账号当前处于待审核状态。请保存你设置的用户名和密码，审核通过后才会启用账号。</p>
        <router-link to="/">返回公众首页</router-link>
      </div>
    </section>
  </main>
</template>

<script>
export default {
  name: 'FarmerApply',
  data() {
    return {
      submitting: false, submitted: false, error: '', file: null,
      form: { username: '', password: '', applicantType: 'INDIVIDUAL',
        organizationName: '', contactName: '', contactPhone: '', farmingLocation: '',
        proofType: 'LAND_CONTRACT', applicationNote: '' }
    };
  },
  methods: {
    onFile(event) {
      this.file = event.target.files[0] || null;
    },
    async submit() {
      if (this.submitting) return;
      if (!this.file || this.file.size > 5 * 1024 * 1024) {
        this.error = '请选择不超过5MB的PDF、PNG或JPG文件';
        return;
      }
      this.submitting = true;
      this.error = '';
      try {
        const data = new FormData();
        Object.entries(this.form).forEach(([key, value]) => data.append(key, value));
        data.append('proofFile', this.file);
        const response = await fetch('/api/public/farmer-applications', {
          method: 'POST', credentials: 'same-origin', body: data
        });
        const result = await response.json();
        if (!response.ok || result.code !== 200) {
          throw new Error(result.message || '申请提交失败');
        }
        this.form.password = '';
        this.submitted = true;
      } catch (error) {
        this.error = error.message || '无法提交，请检查服务器连接';
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>

<style scoped>
.apply-page { min-height: calc(100vh - 130px); padding: 48px 20px; background: #f7f8f4; color: #234a3c; }
.apply-panel { max-width: 780px; margin: 0 auto; padding: 38px 42px; background: #fff; border: 1px solid #e0e9df; border-radius: 15px; }
.eyebrow { color: #7e977f; font-size: 13px; margin: 0; }
h1 { font-size: 30px; margin: 12px 0; }
h2 { font-size: 20px; }
.subtitle { color: #738777; line-height: 1.8; margin-bottom: 20px; }
.notice { background: #f5f2e7; color: #756943; padding: 12px 16px; border-radius: 9px; font-size: 13px; line-height: 1.7; }
form { display: grid; gap: 18px; margin-top: 24px; }
.two-col { display: grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap: 18px; }
label { display: flex; flex-direction: column; gap: 8px; font-size: 14px; }
input, select, textarea { box-sizing: border-box; width: 100%; font: inherit; padding: 11px 12px; border: 1px solid #d4e0d6; border-radius: 8px; background: #fff; color: #274637; }
button { font: inherit; background: #234a3c; color: #fff; border: none; padding: 14px; border-radius: 9px; font-weight: 600; cursor: pointer; }
button:disabled { opacity: .55; }
.error { color: #9e4d48; background: #fff0ee; padding: 10px 14px; border-radius: 8px; }
.success { background: #f1f7ee; padding: 28px; border-radius: 10px; line-height: 1.8; }
.success a { color: #356e4f; }
@media(max-width: 620px) { .apply-panel { padding: 24px 20px; } .two-col { grid-template-columns: 1fr; } }
</style>

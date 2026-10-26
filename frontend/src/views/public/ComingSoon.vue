<template>
  <div class="entry-page">
    <i class="el-icon-notebook-2"></i>
    <h1>{{title}}</h1>
    <p>{{message}}</p>
    <div class="hint">当前尚未启用实名认证、文件提交或后台登录功能。请勿通过本页面提交真实身份证件或账号密码。</div>
    <router-link to="/">返回首页</router-link>
  </div>
</template>
<script>
export default {
  name:'PublicComingSoon',
  computed:{
    title(){return this.$route.path==='/login'?'后台登录':'农户入驻';},
    message(){return this.$route.path==='/login'?'正在建设管理员和审核通过农户的安全登录功能。':'入驻申请将设置资质审核，审核通过后方可录入农产品资料。';}
  }
};
</script>
<style scoped>
.entry-page{max-width:750px;margin:55px auto;background:white;border:1px solid #e5ece3;border-radius:15px;padding:65px 36px;text-align:center}.entry-page>i{font-size:35px;color:#578a65}.entry-page h1{font-size:29px;color:#284d36}.entry-page p{color:#6e8a74;font-size:16px}.hint{background:#f4f6ef;border-radius:8px;padding:18px;font-size:14px;line-height:1.75;color:#6a7e6c;margin:28px 0}.entry-page a{color:#38754d;font-weight:600;text-decoration:none}
</style>

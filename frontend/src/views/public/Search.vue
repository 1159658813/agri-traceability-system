<template>
  <div class="search-view">
    <div class="search-icon"><i class="el-icon-search"></i></div>
    <h1>农产品溯源查询</h1>
    <p>消费者无需注册。查询功能将仅展示通过审核、已经发布的批次信息。</p>
    <form @submit.prevent="submit"><el-input v-model.trim="code" maxlength="80" placeholder="请输入溯源码" clearable/><el-button type="primary" native-type="submit">查询</el-button></form>
    <div v-if="searched" class="status-message">溯源查询接口尚未开放，当前无法验证该编号。请勿将此提示视为产品查验结果。</div>
    <div class="return"><router-link to="/">返回平台首页</router-link></div>
  </div>
</template>
<script>
export default {
  name:'PublicSearch',
  data(){return{code:this.$route.query.code || '',searched:false};},
  methods:{submit(){this.searched=true;}}
};
</script>
<style scoped>
.search-view{max-width:730px;text-align:center;margin:60px auto;background:white;border:1px solid #e6ece4;border-radius:16px;padding:58px 40px}.search-icon{font-size:30px;color:#4b805e}.search-view h1{font-size:29px;color:#294d39}.search-view p{font-size:15px;color:#7c9581;line-height:1.8}.search-view form{display:flex;gap:12px;max-width:560px;margin:30px auto}.search-view form .el-input{flex:1}.status-message{background:#f7f0e4;color:#8e7148;text-align:left;padding:16px 19px;line-height:1.65;font-size:14px;border-radius:8px}.return{margin-top:26px}.return a{color:#3c7351;font-size:14px}@media(max-width:650px){.search-view{padding:35px 18px;margin:20px auto}.search-view form{flex-direction:column}}
</style>

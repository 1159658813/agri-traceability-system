<template>
  <section class="simple-page">
    <div class="simple-heading"><span>田野档案</span><h1>平台公告</h1><p>查看平台对外发布的通知和相关说明。</p></div>
    <div class="paper" v-loading="loading">
      <template v-if="detailMode">
        <router-link to="/notices" class="back">← 返回公告列表</router-link>
        <div v-if="detail" class="notice-detail"><h2>{{ detail.title }}</h2><p class="sub">{{ typeName(detail.noticeType) }} · {{ dateText(detail.publishedAt) }}</p><p class="notice-body">{{detail.content}}</p></div>
        <div v-else-if="!loading" class="empty">{{error || '该公告不可查看'}}</div>
      </template>
      <template v-else>
        <p v-if="error" class="error">{{error}}</p>
        <div v-if="!loading && notices.length===0" class="empty">暂无已发布公告</div>
        <router-link v-for="item in notices" :key="item.id" :to="'/notices/'+item.id" class="notice-row">
          <div><span v-if="item.top" class="tag">置顶</span><strong>{{item.title}}</strong><small>{{typeName(item.noticeType)}}</small></div>
          <span class="date">{{dateText(item.publishedAt)}} <i class="el-icon-arrow-right"></i></span>
        </router-link>
      </template>
    </div>
  </section>
</template>
<script>
import { getNotices, getNotice } from '../../api/public';
export default {
  name:'PublicNotices',
  data(){return{notices:[],detail:null,loading:true,error:''};},
  computed:{ detailMode(){return Boolean(this.$route.params.id);} },
  created(){this.load();},
  watch:{'$route.fullPath'(){this.load();}},
  methods:{
    typeName(v){return ({GENERAL:'普通公告',POLICY:'平台说明',MAINTENANCE:'维护通知'})[v]||'平台公告';},
    dateText(v){return v?String(v).replace('T',' ').slice(0,16):'—';},
    async load(){this.loading=true;this.error='';this.detail=null;this.notices=[];
      try{if(this.detailMode){this.detail=await getNotice(this.$route.params.id);}else{this.notices=await getNotices(20);}}
      catch(e){this.error='公告数据暂时无法读取';}
      finally{this.loading=false;}
    }
  }
};
</script>
<style scoped>
.simple-page{max-width:1050px;margin:0 auto}.simple-heading{margin:15px 0 26px}.simple-heading span{color:#82a085;font-size:14px}.simple-heading h1{font-size:31px;color:#284b37;margin:8px 0}.simple-heading p{color:#839786;font-size:15px}.paper{background:#fff;border:1px solid #e6ece3;border-radius:14px;padding:20px 35px;min-height:280px}.back{text-decoration:none;color:#497c58;display:inline-block;margin-bottom:23px}.notice-row{display:flex;justify-content:space-between;align-items:center;border-bottom:1px solid #eaf0e8;gap:20px;padding:22px 0;text-decoration:none;color:#305440}.notice-row:last-child{border:0}.notice-row strong{font-size:17px;font-weight:650}.notice-row small{display:block;font-size:13px;color:#91a498;margin-top:8px}.tag{display:inline-block;background:#eaf0e2;color:#618267;border-radius:4px;font-size:12px;margin-right:10px;padding:2px 7px}.date{white-space:nowrap;font-size:13px;color:#93a79b}.date i{margin-left:10px}.notice-detail h2{font-size:25px;color:#2c513c}.sub{color:#859b88;border-bottom:1px solid #e8eee5;padding-bottom:18px}.notice-body{white-space:pre-wrap;line-height:2;font-size:16px;color:#445e4d}.empty{padding:80px 0;text-align:center;color:#97a69c;font-size:15px}.error{color:#b16352}@media(max-width:600px){.paper{padding:16px 21px}.notice-row{align-items:flex-start;flex-direction:column;gap:8px}.simple-heading h1{font-size:26px}}
</style>

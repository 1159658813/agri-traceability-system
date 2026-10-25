<template>
  <div class="pending-page"><div class="pending-breadcrumb">业务模块 / {{ item.title }}</div><div class="pending-sheet"><div class="pending-side"><span>业务功能</span><div class="pending-icon"><i :class="item.icon"></i></div><span class="pending-side-bottom">田野档案 · 逐步完善</span></div><div class="pending-body"><div class="pending-flag"><i class="el-icon-time"></i> 功能开发中</div><h1>{{ item.title }}</h1><p>{{ item.description }}</p><div class="pending-line"></div><div class="pending-info"><strong>本模块计划实现</strong><div v-for="(feature,idx) in item.features" :key="idx"><span>{{ String(idx+1).padStart(2,'0') }}</span>{{ feature }}</div></div><el-button class="back-button" icon="el-icon-back" @click="$router.push('/')">返回工作台</el-button></div></div><div class="pending-note"><i class="el-icon-info"></i> 当前仅展示功能规划，尚无对应业务接口及真实数据。界面不会显示虚构统计。</div></div>
</template>
<script>
const MODULES = {
  products: { title:'农产品档案',icon:'el-icon-collection',serial:'04',description:'管理品种、分类和农产品基本特征，逐步形成可维护的产品档案。',features:['分类及农产品基础信息维护','产品与产地信息关联','产品档案查询与筛选'] },
  batches: { title:'溯源批次',icon:'el-icon-tickets',serial:'05',description:'以唯一批次编号为核心，关联产地、产品以及完整生产流通过程。',features:['批次建立与编号生成','批次记录关联及审核','公开溯源码生成'] },
  farm: { title:'农事记录',icon:'el-icon-notebook-2',serial:'06',description:'记录与批次相关的农事操作，为溯源信息提供过程依据。',features:['农事操作分类及日期管理','农事记录上传与关联','记录追踪和查询'] },
  harvest: { title:'采收管理',icon:'el-icon-s-management',serial:'07',description:'整理采收时间、产量和批次归属，连接生产与流通环节。',features:['采收日期与数量登记','采收批次关联','采收信息查询'] },
  quality: { title:'质量检测',icon:'el-icon-circle-check',serial:'08',description:'归集质量检测记录与检测附件，区分实际检测与一般介绍。',features:['质检记录登记','检测材料归档','检测结论公开范围管理'] },
  logistics: { title:'流通记录',icon:'el-icon-truck',serial:'09',description:'为批次补齐出库、运输及流向节点，构建时间线式溯源记录。',features:['流通节点登记','时间及流向记录','批次流通路径展示'] },
  completeness: { title:'完整性评价',icon:'el-icon-data-analysis',serial:'10',description:'依据规则表检查批次资料的完整程度，形成缺失清单与评分结果。',features:['多环节规则与权重设置','缺失项目计算与提示','评价结果快照与展示'] }
};
export default {
  name: 'ModulePending', props:{section:{type:String,required:true}},
  computed:{ item(){return MODULES[this.section]||{title:'待开发模块',description:'',icon:'el-icon-more',serial:'--',features:[]};} }
};
</script>
<style scoped>
.pending-breadcrumb{font-size:11px;color:#78927f;letter-spacing:1px;margin-bottom:21px}.pending-sheet{min-height:490px;background:white;border:1px solid #e8eee7;display:grid;grid-template-columns:210px 1fr;border-radius:15px;overflow:hidden}.pending-side{background:#254c3c;color:#d7e4cd;display:flex;flex-direction:column;padding:30px;justify-content:space-between}.pending-side>span{font-size:10px;letter-spacing:1.6px}.pending-side .pending-icon{font-size:67px;text-align:center;color:#b1c9a6}.pending-side-bottom{opacity:.64}.pending-body{padding:49px 59px}.pending-flag{font-size:11px;color:#87a08b;letter-spacing:1px}.pending-flag i{margin-right:7px}.pending-body h1{color:#264736;font-size:32px;margin:22px 0 11px}.pending-body p{font-size:13px;color:#91a398;max-width:570px;line-height:2}.pending-line{height:1px;background:#e9eee6;margin:33px 0 25px}.pending-info strong{font-size:13px;color:#506d59}.pending-info>div{font-size:12px;color:#6d8273;margin:16px 0}.pending-info>div span{font-family:Georgia,serif;margin-right:14px;color:#a2b8a3}.back-button{margin-top:27px}.pending-note{font-size:11px;color:#a4afa6;margin-top:17px}.pending-note i{margin-right:7px}@media(max-width:780px){.pending-sheet{grid-template-columns:1fr}.pending-side{min-height:96px;flex-direction:row;align-items:center}.pending-side .pending-icon{font-size:32px}.pending-side-bottom{display:none}.pending-body{padding:31px 25px}}

/* V1.1 可读性调整：加大常用字号，保留现有页面布局 */

.pending-breadcrumb { font-size: 13px; letter-spacing: .2px; }
.pending-side > span { font-size: 13px; letter-spacing: .3px; }
.pending-flag { font-size: 14px; letter-spacing: .2px; }
.pending-body p { font-size: 15px; color: #738b79; line-height: 1.9; }
.pending-info strong { font-size: 16px; }
.pending-info > div { font-size: 14px; line-height: 1.6; }
.pending-note { font-size: 13px; line-height: 1.6; }
@media (max-width: 780px) {
  .pending-body { padding: 31px 24px; }
}

</style>

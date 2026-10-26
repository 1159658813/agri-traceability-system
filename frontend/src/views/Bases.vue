<template>
  <div class="bases">
    <div class="crumb">种植基地管理</div>
    <div class="page-intro"><div><h1>种植基地</h1><p>建立产地基础档案，为农产品批次关联提供准确的地理来源。</p></div><div class="intro-accent"><i class="el-icon-location-outline"></i><span>基地档案</span></div></div>
    <div class="list-panel">
      <div class="list-head"><div><div class="panel-eyebrow">基地记录</div><h2>基地档案列表 <span>{{ error ? '—' : total }} 条记录</span></h2></div><div class="list-actions"><el-input v-model.trim="keyword" size="small" clearable placeholder="筛选当前页基地名称 / 地区" prefix-icon="el-icon-search" class="search-input"/><el-button size="small" icon="el-icon-refresh" :loading="loading" @click="load">刷新</el-button></div></div>
      <div class="hint-bar"><i class="el-icon-info"></i><span>当前已接入分页查询；搜索仅筛选本页已加载的数据，新增和编辑接口将在后续阶段开放。</span></div>
      <div class="table-wrap">
        <el-table :data="visibleRecords" v-loading="loading" :header-cell-style="{background:'#f7f9f5',color:'#758a78',fontWeight:'600',fontSize:'14px',height:'49px'}" style="width:100%" :row-style="{height:'60px'}" empty-text="本页没有匹配的基地数据">
          <el-table-column label="基地名称" min-width="195"><template slot-scope="scope"><div class="name-col"><span class="name-mark">{{ (scope.row.baseName || '基').slice(0,1) }}</span><span><strong>{{ scope.row.baseName || '未命名基地' }}</strong><small>编号 {{ scope.row.id }}</small></span></div></template></el-table-column>
          <el-table-column label="所属地区" min-width="195"><template slot-scope="scope">{{ region(scope.row) }}</template></el-table-column>
          <el-table-column label="种植面积" width="122"><template slot-scope="scope">{{ area(scope.row.areaMu) }}</template></el-table-column>
          <el-table-column label="基地状态" width="110"><template slot-scope="scope"><span class="table-status" :class="{inactive:scope.row.status !== 1}"><i></i>{{ scope.row.status === 1 ? '正常' : '停用' }}</span></template></el-table-column>
          <el-table-column label="创建时间" width="164"><template slot-scope="scope">{{ dateText(scope.row.createdAt) }}</template></el-table-column>
          <el-table-column label="操作" width="90" align="right"><template slot-scope="scope"><el-button type="text" @click="openDetail(scope.row)">查看详情 <i class="el-icon-arrow-right"></i></el-button></template></el-table-column>
        </el-table>
      </div>
      <div v-if="error" class="api-error"><i class="el-icon-warning-outline"></i> {{ error }} <el-button type="text" @click="load">重试</el-button></div>
      <div class="table-bottom"><span>基地信息来自系统数据库</span><el-pagination background layout="prev, pager, next" :current-page="pageNum" :page-size="pageSize" :total="total" :disabled="loading" @current-change="changePage"/></div>
    </div>
    <div class="below-note"><i class="el-icon-lock"></i> 当前为本地开发预览。正式上线前必须先完成登录认证、角色权限及接口访问控制。</div>
    <el-drawer :visible.sync="detailOpen" direction="rtl" :size="drawerWidth" :with-header="false" custom-class="base-drawer" append-to-body><div v-if="selected" class="detail"><div class="detail-head"><div class="panel-eyebrow">产地档案</div><button @click="detailOpen=false" aria-label="关闭"><i class="el-icon-close"></i></button></div><div class="detail-title"><span class="detail-emblem"><i class="el-icon-map-location"></i></span><h2>{{ selected.baseName }}</h2><p>基地编号 · {{ selected.id }}</p></div><div class="detail-group"><div class="detail-label">地理信息</div><dl><dt>省份</dt><dd>{{ selected.province || '未填写' }}</dd><dt>城市</dt><dd>{{ selected.city || '未填写' }}</dd><dt>区县</dt><dd>{{ selected.district || '未填写' }}</dd><dt>详细地址</dt><dd>{{ selected.address || '未填写' }}</dd></dl></div><div class="detail-group"><div class="detail-label">种植与管理</div><dl><dt>种植面积</dt><dd>{{ area(selected.areaMu) }}</dd><dt>基地状态</dt><dd>{{ selected.status===1?'正常':'停用' }}</dd><dt>创建时间</dt><dd>{{ dateText(selected.createdAt) }}</dd><dt>最后更新</dt><dd>{{ dateText(selected.updatedAt) }}</dd></dl></div><div class="detail-group"><div class="detail-label">基地介绍</div><p class="detail-description">{{ selected.description || '暂无基地介绍' }}</p></div><div class="detail-foot">记录来自数据库，当前界面不修改原始资料。</div></div></el-drawer>
  </div>
</template>
<script>
import { getBasePage } from '../api/bases';
export default {
  name: 'Bases',
  data() { return { loading: false, error: '', records: [], total: 0, pageNum: 1, pageSize: 10, keyword: '', detailOpen: false, selected: null, windowWidth: window.innerWidth }; },
  computed: {
    visibleRecords() { const q = this.keyword.toLowerCase(); return !q ? this.records : this.records.filter(item => [item.baseName,item.province,item.city,item.district,item.address].filter(Boolean).join(' ').toLowerCase().includes(q)); },
    drawerWidth() { return this.windowWidth < 600 ? '92%' : '430px'; }
  },
  mounted() { window.addEventListener('resize', this.onResize); this.load(); },
  beforeDestroy() { window.removeEventListener('resize', this.onResize); },
  methods: {
    onResize() { this.windowWidth = window.innerWidth; },
    async load() {
      this.loading = true; this.error = '';
      try {
        const res = await getBasePage(this.pageNum, this.pageSize);
        const body = res.data;
        const page = body && body.data;
        if (!page || !Array.isArray(page.records)) throw new Error('后端返回结构与当前页面不一致');
        this.records = page.records; this.total = Number(page.total) || 0;
      } catch(e) { this.records = []; this.total = 0; this.error = '获取基地数据失败，请确认后端已启动并支持分页接口。'; }
      finally { this.loading = false; }
    },
    changePage(page) { this.pageNum = page; this.keyword = ''; this.load(); },
    openDetail(row) { this.selected = row; this.detailOpen = true; },
    region(row) { return [row.province,row.city,row.district].filter(Boolean).join(' / ') || '未填写地区'; },
    area(value) { return value == null ? '未填写' : `${value} 亩`; },
    dateText(value) { return value ? String(value).replace('T', ' ').slice(0,16) : '—'; }
  }
};
</script>
<style scoped>
.crumb{font-size:11px;letter-spacing:1.8px;color:#6f9076;font-weight:700;margin:1px 0 19px}.crumb span{color:#b9cabc;margin:0 8px}.page-intro{display:flex;align-items:center;justify-content:space-between;margin-bottom:27px}.page-intro h1{font-size:29px;letter-spacing:.5px;color:#263e31;margin:0}.page-intro h1 small{font-size:12px;color:#a8b7a5;font-weight:400;margin-left:11px;letter-spacing:.5px}.page-intro p{font-size:12px;color:#97a598;margin:12px 0 0}.intro-accent{height:52px;display:flex;align-items:center;gap:9px;color:#a3b6a6;font-size:10px;letter-spacing:1.5px}.intro-accent i{font-size:27px;color:#6e9878}.list-panel{background:white;border:1px solid #e7ebe5;border-radius:14px;overflow:hidden;box-shadow:0 5px 20px rgba(31,68,47,.025)}.list-head{display:flex;justify-content:space-between;align-items:center;padding:28px 28px 23px;gap:15px}.panel-eyebrow{font-size:10px;color:#85a08a;font-weight:750;letter-spacing:1.55px}.list-head h2{font-size:19px;color:#2e4637;margin:7px 0 0}.list-head h2 span{font-size:11px;font-weight:400;color:#9eafa2;margin-left:10px}.list-actions{display:flex;gap:10px}.search-input{width:277px}.hint-bar{padding:10px 27px;background:#f6f9f3;color:#91a28f;font-size:11px;display:flex;align-items:center;gap:8px;border-top:1px solid #eff2ec;border-bottom:1px solid #eff2ec}.hint-bar i{color:#7c9d84;font-size:13px}.table-wrap{padding:0 21px}.name-col{display:flex;align-items:center;gap:11px}.name-mark{width:33px;height:33px;flex:0 0 33px;background:#e9efe5;color:#5a8667;border-radius:9px;display:grid;place-items:center;font-weight:700}.name-col span:last-child{display:flex;flex-direction:column;gap:4px}.name-col strong{font-size:12px;color:#3b5141;font-weight:650}.name-col small{font-size:10px;color:#9dae9f}.table-status{display:inline-flex;align-items:center;padding:5px 10px;border-radius:30px;background:#eaf3e8;color:#5a9470;font-size:11px}.table-status i{height:5px;width:5px;background:#75aa85;border-radius:100%;margin-right:6px}.table-status.inactive{background:#f3f0ea;color:#9b917d}.table-status.inactive i{background:#b5a58b}.table-bottom{display:flex;align-items:center;justify-content:space-between;padding:19px 24px 24px;border-top:1px solid #f0f2ed;margin-top:7px;gap:16px}.table-bottom span{font-size:10px;color:#a5b2a6}.table-bottom code{font-size:10px;color:#7f9782}.api-error{padding:8px 29px;color:#b67d6d;font-size:12px;display:flex;align-items:center;gap:9px}.below-note{font-size:11px;color:#a3b0a5;margin-top:20px}.below-note i{margin-right:7px}.detail{padding:27px 28px;min-height:100%;display:flex;flex-direction:column}.detail-head{display:flex;justify-content:space-between;align-items:center}.detail-head button{border:0;background:transparent;font-size:17px;color:#7e9982;cursor:pointer}.detail-title{padding:40px 0 25px;border-bottom:1px solid #edf0eb}.detail-emblem{height:54px;width:54px;background:#e3efdf;color:#5d8e68;border-radius:13px;display:grid;place-items:center;font-size:25px;margin-bottom:19px}.detail-title h2{font-size:24px;color:#254535;margin:0}.detail-title p{font-size:12px;color:#9fae9f}.detail-group{margin:24px 0 2px}.detail-label{font-size:12px;letter-spacing:1.2px;color:#54745a;font-weight:750;margin-bottom:14px}.detail-group dl{display:grid;grid-template-columns:95px 1fr;row-gap:16px;margin:0;font-size:12px}.detail-group dt{color:#a0ae9f}.detail-group dd{margin:0;color:#3e5443;line-height:1.6}.detail-description{font-size:12px;color:#59705e;line-height:2;margin:0}.detail-foot{margin-top:auto;border-top:1px solid #e7eee5;padding-top:20px;font-size:11px;color:#a0b09f}@media(max-width:1200px){.list-head{flex-direction:column;align-items:stretch}.list-actions{justify-content:space-between}.search-input{width:min(100%,380px)}}@media(max-width:600px){.page-intro h1{font-size:23px}.page-intro h1 small{display:block;margin:6px 0 0}.intro-accent{display:none}.list-head{padding:21px 16px 16px}.list-actions{flex-wrap:wrap}.search-input{width:100%}.hint-bar{padding:12px 15px;line-height:1.6}.table-wrap{overflow-x:auto;padding:0 12px}.table-wrap>>>.el-table{min-width:770px}.table-bottom{padding:18px 12px;flex-wrap:wrap}.table-bottom span{display:none}}

/* V1.1 可读性调整：加大常用字号，保留现有页面布局 */

.crumb { font-size: 13px; letter-spacing: .3px; }
.page-intro h1 { font-size: 31px; }
.page-intro p { font-size: 15px; color: #728a78; line-height: 1.6; }
.intro-accent { font-size: 13px; letter-spacing: .3px; }
.panel-eyebrow { font-size: 13px; letter-spacing: .3px; }
.list-head h2 { font-size: 21px; }
.list-head h2 span { font-size: 14px; }
.hint-bar { font-size: 13px; line-height: 1.6; color: #738873; }
.hint-bar i { font-size: 15px; }
.name-col strong { font-size: 14px; }
.name-col small { font-size: 12px; }
.table-status { font-size: 13px; }
.table-bottom span, .table-bottom code { font-size: 12px; }
.api-error { font-size: 13px; }
.below-note { font-size: 13px; line-height: 1.7; }
.detail-title p { font-size: 14px; }
.detail-label { font-size: 15px; letter-spacing: .3px; }
.detail-group dl { font-size: 14px; grid-template-columns: 105px 1fr; }
.detail-description { font-size: 14px; line-height: 1.9; }
.detail-foot { font-size: 13px; line-height: 1.6; }
@media (max-width: 600px) {
  .page-intro h1 { font-size: 26px; }
}

</style>

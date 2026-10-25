<template>
  <div class="dashboard">
    <div class="eyebrow-row"><span>工作台概览</span></div>
    <section class="hero">
      <div class="hero-content"><span class="hero-sub">农产品产地溯源管理</span><h1>记录产地的每一步，<br><em>让溯源有据可循。</em></h1><p>从种植基地开始，逐步串联农事、采收、质检与流通信息。</p><div class="hero-bottom"><button @click="$router.push('/bases')">进入种植基地 <i class="el-icon-right"></i></button></div></div>
      <svg class="hero-landscape" viewBox="0 0 600 340" preserveAspectRatio="xMidYMid slice" aria-hidden="true"><circle cx="416" cy="110" r="57" fill="#d8bd83" opacity=".92"/><path d="M0 310Q160 210 310 276T630 225V360H0" fill="#38674e"/><path d="M0 335Q180 245 380 305T630 273V370H0" fill="#4c7a58"/><path d="M-20 365Q210 299 400 351T650 330V405H-20" fill="#618d65"/><path d="M10 358C170 304 324 335 460 362M95 368C227 340 342 359 487 379M210 390C300 365 410 390 590 400" stroke="#a6bb8d" stroke-width="2" fill="none" opacity=".55"/><path d="M357 254V174m0 27c-38-10-37-47-37-47 34 4 42 22 37 47Zm0-22c22-36 53-34 53-34-4 31-20 45-53 48Zm95 77V196m0 23c-27-8-32-35-32-35 28 2 36 22 32 35Zm0-18c21-27 40-25 40-25-3 24-17 36-40 38Z" stroke="#c0d09d" stroke-width="4" fill="none" stroke-linecap="round" stroke-linejoin="round" opacity=".85"/><path d="M35 272C147 221 245 243 341 258" fill="none" stroke="#7da080" opacity=".45" stroke-dasharray="4 10"/><path d="M290 67h70m-35-35v70" stroke="#a2b9a7" opacity=".25" stroke-width="1"/></svg>
      
    </section>

    <div class="section-head metrics-head"><div><span class="section-index">系统数据</span><h2>系统概况</h2><p>以下状态和数据均由当前后端接口返回</p></div><button class="text-action" :disabled="loading" @click="refresh"><i class="el-icon-refresh" :class="{'is-spin':loading}"></i> 刷新数据</button></div>
    <div class="metrics">
      <div class="metric-card"><div class="metric-icon green"><i class="el-icon-map-location"></i></div><div class="metric-flag">基地管理</div><div class="metric-title">已登记种植基地</div><div class="metric-value">{{ basesError ? '—' : (loading ? '…' : total) }}<span>个基地</span></div><div class="metric-foot">{{ basesError ? '未能读取基地数据' : '来自基地分页接口' }}</div></div>
      <div class="metric-card"><div class="metric-icon gold"><i class="el-icon-connection"></i></div><div class="metric-flag">运行状态</div><div class="metric-title">后端服务状态</div><div class="metric-status"><span class="status-light" :class="{down:!statusOk}"></span>{{ statusLoading ? '检查中' : (statusOk ? '连接正常' : '未连接') }}</div><div class="metric-foot">Spring Boot · 本地端口 8080</div></div>
      <div class="metric-card"><div class="metric-icon gray"><i class="el-icon-s-flag"></i></div><div class="metric-flag">系统版本</div><div class="metric-title">当前开发版本</div><div class="metric-value version">V1<span>基础管理</span></div><div class="metric-foot">仅接入已完成的后端功能</div></div>
    </div>

    <div class="content-grid">
      <section class="panel recent-panel"><div class="panel-top"><div><span class="section-index">基地档案</span><h2>最近登记的基地</h2></div><button class="subtle-link" @click="$router.push('/bases')">查看全部 <i class="el-icon-right"></i></button></div>
        <div v-if="loading" class="panel-empty"><i class="el-icon-loading"></i> 正在从数据库读取…</div><div v-else-if="basesError" class="panel-empty error"><i class="el-icon-warning-outline"></i><span>读取失败，请先运行 Spring Boot 后端</span><button @click="refresh">重新连接</button></div><div v-else-if="recentBases.length === 0" class="panel-empty">数据库中暂时没有基地记录</div>
        <div v-else class="base-list"><button v-for="(item,index) in recentBases" :key="item.id" class="recent-row" @click="$router.push('/bases')"><span class="base-number">{{ String(index + 1).padStart(2, '0') }}</span><span class="base-info"><b>{{item.baseName || '未命名基地'}}</b><small>{{region(item)}}</small></span><span class="base-area">{{area(item.areaMu)}}</span><i class="el-icon-right"></i></button></div>
      </section>
      <section class="panel project-panel"><div class="panel-top"><div><span class="section-index">开发计划</span><h2>功能建设进度</h2></div></div><div class="roadmap"><div class="roadmap-row ready"><span class="roadmap-dot"><i class="el-icon-check"></i></span><div><strong>后端基础服务</strong><small>Spring Boot / MyBatis-Plus / MySQL</small></div><b>已集成</b></div><div class="roadmap-row ready"><span class="roadmap-dot"><i class="el-icon-check"></i></span><div><strong>种植基地查询</strong><small>分页读取和基础展示</small></div><b>已接入</b></div><div class="roadmap-row"><span class="roadmap-dot"><i class="el-icon-more"></i></span><div><strong>批次溯源查询</strong><small>唯一批次编号与公众页面</small></div><b>待开发</b></div><div class="roadmap-row"><span class="roadmap-dot"><i class="el-icon-more"></i></span><div><strong>完整性评价</strong><small>规则评分与缺失项提示</small></div><b>待开发</b></div></div><button class="trace-promo" @click="$router.push('/trace')"><span><strong>查看消费者端页面预览</strong><small>尚未接入真实批次查询</small></span><i class="el-icon-right"></i></button></section>
    </div>
    <div class="dashboard-bottom"><span>本页面不使用模拟业务统计，接口无法访问时会如实显示连接异常。</span><span>© 田野档案</span></div>
  </div>
</template>
<script>
import { getBasePage } from '../api/bases';
import { getSystemStatus } from '../api/system';
export default {
  name: 'Dashboard',
  data() { return { loading: true, basesError: false, total: 0, recentBases: [], statusOk: false, statusLoading: true }; },
  created() { this.refresh(); },
  methods: {
    async refresh() {
      this.loading = true;
      this.statusLoading = true;
      const status = getSystemStatus().then(res => { this.statusOk = res.data && res.data.code === 200; }).catch(() => { this.statusOk = false; }).finally(() => { this.statusLoading = false; });
      const bases = getBasePage(1, 5).then(res => {
        const payload = res.data && res.data.data;
        if (!payload || !Array.isArray(payload.records)) throw new Error('基地分页接口结构不正确');
        this.recentBases = payload.records;
        this.total = Number(payload.total) || 0;
        this.basesError = false;
      }).catch(() => { this.recentBases = []; this.basesError = true; }).finally(() => { this.loading = false; });
      await Promise.all([status, bases]);
    },
    region(item) { return [item.province, item.city, item.district].filter(Boolean).join(' · ') || '未设置地区'; },
    area(value) { return value == null ? '面积未录入' : `${value} 亩`; }
  }
};
</script>
<style scoped>
.eyebrow-row{display:flex;justify-content:space-between;align-items:center;font-size:10px;letter-spacing:2.2px;color:#68846f;font-weight:750;margin:2px 1px 19px}.edition{font-size:10px;color:#b0b9aa;letter-spacing:1.3px}.hero{height:290px;position:relative;background:#234738;border-radius:17px;overflow:hidden;color:#fff;box-shadow:0 12px 27px rgba(24,61,42,.10)}.hero-content{position:relative;z-index:2;padding:39px 48px}.hero-sub{font-size:10px;color:#b9ceba;letter-spacing:1.8px;font-weight:600;display:flex;align-items:center;gap:13px}.hero-sub i{width:31px;height:1px;background:#8cae8f;display:inline-block}.hero h1{font-size:31px;line-height:1.47;letter-spacing:1.3px;font-weight:680;margin:20px 0 10px}.hero h1 em{font-style:normal;color:#d6bd87}.hero p{font-size:12px;color:#bad0c1;letter-spacing:.3px}.hero-bottom{display:flex;align-items:center;gap:22px;margin-top:24px}.hero-bottom button{font:inherit;background:#e5c994;border:0;border-radius:7px;color:#234437;padding:11px 19px;font-size:12px;font-weight:700;cursor:pointer}.hero-bottom button i{margin-left:15px}.hero-bottom span{font-size:10px;color:#a9c4b0}.hero-landscape{position:absolute;height:100%;width:56%;right:0;bottom:0;opacity:.93}.hero-coordinates{position:absolute;z-index:2;right:28px;top:25px;font-family:Georgia,serif;color:#a7c1ab;letter-spacing:2px;font-size:9px}.section-head{display:flex;justify-content:space-between;align-items:end}.metrics-head{margin:31px 0 18px}.section-index{font-size:10px;letter-spacing:1.3px;color:#8a9e8a;font-weight:750}.section-head h2,.panel-top h2{font-size:20px;letter-spacing:.4px;color:#293e33;margin:7px 0 5px}.section-head p{font-size:12px;color:#9aa59a;margin:0}.text-action{background:transparent;color:#466750;border:0;font-size:12px;cursor:pointer}.text-action:disabled{opacity:.5;cursor:wait}.text-action i{margin-right:6px}.metrics{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:17px}.metric-card{min-height:181px;background:white;border:1px solid #e7ebe4;border-radius:13px;padding:22px 25px;position:relative}.metric-icon{width:37px;height:37px;border-radius:10px;display:grid;place-items:center;font-size:18px}.metric-icon.green{background:#e6f0e5;color:#547b59}.metric-icon.gold{background:#fbf2e3;color:#be9650}.metric-icon.gray{background:#eef1ea;color:#6d8a71}.metric-flag{position:absolute;top:27px;right:24px;font-size:9px;color:#adb8ab;letter-spacing:1.1px}.metric-title{margin-top:18px;color:#819184;font-size:12px}.metric-value{font-size:35px;color:#2a4738;font-weight:720;margin-top:5px;line-height:1.25}.metric-value span{font-size:12px;color:#9ba99b;font-weight:400;margin-left:7px}.metric-value.version{font-size:29px;letter-spacing:1px}.metric-status{font-size:23px;color:#2a4738;font-weight:650;display:flex;gap:10px;align-items:center;margin-top:11px}.status-light{width:9px;height:9px;background:#69a780;border-radius:50%;box-shadow:0 0 0 4px #e4f2e7}.status-light.down{background:#bc8b6f;box-shadow:0 0 0 4px #f9efea}.metric-foot{font-size:11px;color:#adbaad;margin-top:7px}.content-grid{display:grid;grid-template-columns:1.17fr 1fr;gap:18px;margin-top:22px}.panel{background:#fff;border:1px solid #e7ebe4;border-radius:13px;min-height:330px;overflow:hidden}.panel-top{display:flex;justify-content:space-between;align-items:center;padding:24px 26px 17px}.panel-top h2{font-size:17px;margin-top:6px}.subtle-link{border:0;background:transparent;color:#497456;font-size:12px;cursor:pointer}.subtle-link i{margin-left:7px}.base-list{padding:0 17px 12px}.recent-row{display:flex;gap:16px;width:100%;align-items:center;text-align:left;padding:13px 10px;background:#fff;border:0;border-bottom:1px solid #f0f2ed;cursor:pointer}.recent-row:hover{background:#f8faf7}.recent-row:last-child{border-bottom:0}.base-number{font:16px Georgia,serif;color:#b7c8b6;width:22px}.base-info{display:flex;flex-direction:column;flex:1;gap:4px;min-width:0}.base-info b{font-size:13px;color:#30483a;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.base-info small{font-size:11px;color:#a1ada1}.base-area{font-size:11px;color:#738778}.recent-row>i{color:#a8b7a9;font-size:12px}.panel-empty{display:flex;align-items:center;justify-content:center;gap:9px;min-height:198px;color:#9cae9f;font-size:12px;flex-wrap:wrap;padding:30px}.panel-empty.error{color:#b17c6d}.panel-empty button{border:0;border-bottom:1px solid #9ab09e;color:#688d72;cursor:pointer;background:none}.micro-label{font-size:10px;color:#9dae9e;letter-spacing:1px}.roadmap{padding:0 28px 3px}.roadmap-row{display:flex;align-items:center;gap:12px;padding:12px 0;border-bottom:1px dashed #edf0eb}.roadmap-row:last-child{border:0}.roadmap-dot{height:26px;width:26px;border-radius:50%;display:grid;place-items:center;background:#f1f3ee;color:#a8b5aa;font-size:11px}.roadmap-row.ready .roadmap-dot{background:#e5f0e4;color:#588867}.roadmap-row div{display:flex;flex:1;flex-direction:column;gap:3px}.roadmap-row strong{font-size:12px;color:#44574a}.roadmap-row small{font-size:10px;color:#acb7ab}.roadmap-row b{font-size:10px;font-weight:500;color:#a5afa5}.roadmap-row.ready b{color:#5b9970}.trace-promo{background:#f0f4ec;width:calc(100% - 52px);margin:9px 26px 21px;padding:12px 15px;display:flex;justify-content:space-between;align-items:center;border-radius:8px;border:0;text-align:left;cursor:pointer;color:#507656}.trace-promo span{display:flex;flex-direction:column;gap:4px}.trace-promo strong{font-size:12px}.trace-promo small{color:#92a691;font-size:10px}.dashboard-bottom{display:flex;justify-content:space-between;font-size:10px;color:#adb6aa;margin-top:19px}.is-spin{animation:spin 1s infinite linear}@keyframes spin{to{transform:rotate(360deg)}}@media(max-width:1300px){.content-grid{grid-template-columns:1fr}.hero-landscape{width:50%}}@media(max-width:850px){.metrics{grid-template-columns:1fr 1fr}.hero-content{padding:30px}.hero h1{font-size:26px}.hero-landscape{opacity:.35;width:75%}}@media(max-width:600px){.metrics{grid-template-columns:1fr}.hero{height:310px}.hero h1{font-size:25px}.hero-content{padding:28px 24px}.edition{display:none}.hero-sub{font-size:9px;letter-spacing:1px}.hero-bottom{gap:12px;flex-wrap:wrap}.hero-bottom span{font-size:9px}.section-head h2{font-size:19px}.metric-card{min-height:158px}.dashboard-bottom span:last-child{display:none}.panel-top{padding:20px}.base-list{padding:0 12px 10px}.roadmap{padding:0 18px}.trace-promo{margin-left:18px;margin-right:18px;width:calc(100% - 36px)}}

/* V1.1 可读性调整：加大常用字号，保留现有页面布局 */

.eyebrow-row { font-size: 13px; letter-spacing: .3px; margin-bottom: 16px; }
.hero { min-height: 315px; height: auto; }
.hero-content { padding: 39px 48px 40px; max-width: 790px; }
.hero-sub { font-size: 13px; letter-spacing: 1px; }
.hero h1 { font-size: 35px; line-height: 1.46; }
.hero p { font-size: 15px; line-height: 1.8; max-width: 530px; }
.hero-bottom button { font-size: 14px; padding: 12px 20px; }
.section-index { font-size: 13px; letter-spacing: .3px; }
.section-head h2, .panel-top h2 { font-size: 21px; }
.section-head p { font-size: 14px; line-height: 1.6; color: #788f7b; }
.text-action, .subtle-link { font-size: 14px; }
.metric-card { padding: 23px 25px; min-height: 196px; }
.metric-flag { font-size: 12px; letter-spacing: .2px; }
.metric-title { font-size: 14px; color: #627a68; }
.metric-value span { font-size: 14px; }
.metric-foot { font-size: 13px; line-height: 1.5; color: #819687; }
.panel-top h2 { font-size: 19px; }
.base-info b { font-size: 15px; }
.base-info small { font-size: 13px; color: #7c9181; }
.base-area { font-size: 13px; }
.recent-row { padding-top: 15px; padding-bottom: 15px; }
.panel-empty { font-size: 14px; }
.roadmap-row { padding: 15px 0; }
.roadmap-row div { gap: 5px; }
.roadmap-row strong { font-size: 15px; line-height: 1.35; }
.roadmap-row small { font-size: 13px; color: #7d9081; line-height: 1.5; }
.roadmap-row b { font-size: 13px; white-space: nowrap; }
.roadmap-dot { width: 29px; height: 29px; font-size: 13px; }
.trace-promo { padding: 15px 17px; }
.trace-promo strong { font-size: 14px; }
.trace-promo small { font-size: 13px; }
.dashboard-bottom { font-size: 12px; line-height: 1.5; }
@media (max-width: 850px) {
  .hero-content { padding: 31px; }
  .hero h1 { font-size: 29px; }
}
@media (max-width: 600px) {
  .hero { min-height: 340px; height: auto; }
  .hero-content { padding: 29px 24px; }
  .hero h1 { font-size: 28px; }
  .hero-sub { font-size: 12px; }
  .hero p { font-size: 14px; }
  .section-head h2 { font-size: 21px; }
  .roadmap-row { gap: 9px; }
  .roadmap-row strong { font-size: 14px; }
  .roadmap-row small, .roadmap-row b { font-size: 12px; }
}

</style>

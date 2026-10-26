<template>
  <div class="portal-home">
    <section class="home-hero">
      <div class="hero-copy">
        <span class="eyebrow">产地信息公开 · 农产品溯源查询</span>
        <h1>了解一份农产品，<br><em>从它的产地开始。</em></h1>
        <p>查看已公开批次的产地与生产记录，让每一次查询都有明确的数据来源。</p>
        <form class="search-box" @submit.prevent="goSearch">
          <i class="el-icon-search"></i>
          <input v-model.trim="traceCode" aria-label="溯源码" maxlength="80" placeholder="输入产品溯源码进行查询">
          <button type="submit">开始查询 <i class="el-icon-right"></i></button>
        </form>
        <small>目前尚未开放真实批次查询功能，未公开的记录不会展示。</small>
      </div>
      <svg class="hero-art" viewBox="0 0 400 300" aria-hidden="true">
        <circle cx="255" cy="94" r="63" fill="#d6bf8b"/>
        <path d="M0 250Q95 195 195 242T405 210V315H0" fill="#35684c"/><path d="M-20 278Q110 216 260 272T440 252V330H-20" fill="#47785a"/>
        <g stroke="#b7cba2" stroke-width="4" fill="none" stroke-linecap="round" stroke-linejoin="round">
          <path d="M205 238v-120m0 49c-45-11-49-52-49-52 34 4 53 21 49 52Zm0-24c20-37 56-41 56-41-2 31-24 49-56 52Z"/>
          <path d="M317 246v-81m0 31c-29-12-35-37-35-37 28 3 38 19 35 37Zm0-16c15-25 39-30 39-30-1 24-15 35-39 38Z"/>
        </g>
      </svg>
    </section>

    <section class="numbers-section" aria-labelledby="number-title">
      <div class="section-heading"><div><span class="section-kicker">公开数据</span><h2 id="number-title">平台收录概览</h2><p>根据已发布的溯源批次实时统计，不包含未审核或草稿信息</p></div><button :disabled="loading" @click="load" class="refresh"><i class="el-icon-refresh" :class="{'el-icon-loading':loading}"></i> 刷新数据</button></div>
      <div v-if="error" class="feedback error">{{error}} <button @click="load">重新加载</button></div>
      <div class="number-grid" v-loading="loading">
        <article v-for="(item,i) in metricCards" :key="item.key" class="number-card">
          <div class="number-symbol"><i :class="item.icon"></i></div>
          <p>{{item.title}}</p><strong>{{hasOverview ? numberValue(item.key) : '—'}}</strong>
          <span>{{item.unit}}</span><b>0{{i+1}}</b>
        </article>
      </div>
    </section>

    <section class="charts-section" aria-labelledby="chart-title">
      <div class="section-heading"><div><span class="section-kicker">数据观察</span><h2 id="chart-title">公开溯源数据分布</h2><p>当前展示数据为开发测试所用的模拟记录</p></div></div>
      <div class="chart-grid">
        <article class="chart-card"><div class="card-heading"><h3>近六个月公开批次</h3><span>按首次发布时间统计</span></div><div v-if="trendTotal===0" class="empty-chart">暂无公开批次趋势数据</div><div v-show="trendTotal>0" ref="trendChart" class="chart-body" aria-label="近六个月公开批次柱状图"></div></article>
        <article class="chart-card"><div class="card-heading"><h3>农产品分类分布</h3><span>已公开批次所属分类</span></div><div v-if="categories.length===0" class="empty-chart">暂无已公开的产品分类数据</div><div v-show="categories.length>0" ref="categoryChart" class="chart-body" aria-label="公开批次分类柱状图"></div></article>
      </div>
    </section>

    <section class="bottom-grid">
      <article class="notice-card"><div class="card-heading"><div><span class="section-kicker">平台动态</span><h3>最新公告</h3></div><router-link to="/notices">全部公告 <i class="el-icon-right"></i></router-link></div>
        <div v-if="notices.length===0" class="notice-empty">{{noticeError ? '公告暂时无法读取' : '暂无已发布公告'}}</div>
        <router-link v-for="item in notices" :key="item.id" class="notice-item" :to="'/notices/'+item.id"><span class="notice-dot"></span><span class="notice-text"><b>{{item.title}}</b><small>{{formatDate(item.publishedAt)}}</small></span><i class="el-icon-arrow-right"></i></router-link>
      </article>
      <article class="guide-card"><span class="section-kicker">使用指引</span><h3>如何查询农产品来源？</h3><p>消费者无需创建账号。待批次查询功能上线后，可通过产品包装上的溯源码或二维码查看已审核发布的信息。</p><router-link to="/search">了解溯源查询 <i class="el-icon-right"></i></router-link><div class="guide-stamp"><i class="el-icon-map-location"></i></div></article>
    </section>
  </div>
</template>
<script>

import * as echarts from 'echarts/core';
import { BarChart } from 'echarts/charts';
import { GridComponent, TooltipComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

echarts.use([
  BarChart,
  GridComponent,
  TooltipComponent,
  CanvasRenderer
]);
import { getOverview, getTrend, getCategories, getNotices } from '../../api/public';
const METRICS = [
  { key:'publishedBatches', title:'公开溯源批次', unit:'个批次', icon:'el-icon-tickets' },
  { key:'productCount', title:'已公开农产品', unit:'种产品', icon:'el-icon-collection' },
  { key:'baseCount', title:'参与种植基地', unit:'个基地', icon:'el-icon-map-location' },
  { key:'regionCount', title:'覆盖产地', unit:'个省市组合', icon:'el-icon-location-outline' }
];
export default {
  name:'PublicHome',
  data() { return { metricCards:METRICS, loading:true, hasOverview:false, overview:{}, trend:[], categories:[], notices:[], error:'', noticeError:false, traceCode:'', charts:[] }; },
  computed: { trendTotal() { return this.trend.reduce((n,item)=>n+Number(item.value||0),0); } },
  mounted() { this.load(); window.addEventListener('resize', this.resizeCharts); },
  beforeDestroy() { window.removeEventListener('resize', this.resizeCharts); this.disposeCharts(); },
  methods: {
    numberValue(key) { return Number(this.overview[key] || 0).toLocaleString('zh-CN'); },
    formatDate(value) { return value ? String(value).replace('T',' ').slice(0,10) : ''; },
    goSearch() { this.$router.push({ path:'/search', query: this.traceCode ? { code:this.traceCode } : {} }); },
    async load() {
      this.loading=true; this.error=''; this.noticeError=false;
      try {
        const [overview,trend,categories] = await Promise.all([getOverview(), getTrend(), getCategories()]);
        this.overview=overview||{}; this.hasOverview=true;
        this.trend=Array.isArray(trend)?trend:[];
        this.categories=Array.isArray(categories)?categories:[];
        this.$nextTick(this.renderCharts);
      } catch(e) {
        this.hasOverview=false; this.trend=[]; this.categories=[];
        this.error='公开统计数据暂时无法读取，请检查后端服务。';
        this.disposeCharts();
      } finally { this.loading=false; }
      try { const data=await getNotices(5); this.notices=Array.isArray(data)?data:[]; }
      catch(e) { this.noticeError=true; this.notices=[]; }
    },
    disposeCharts() { this.charts.forEach(c=>c.dispose()); this.charts=[]; },
    resizeCharts() { this.charts.forEach(c=>c.resize()); },
    renderCharts() {
      this.disposeCharts();
      const common={animationDuration:450,tooltip:{trigger:'axis'},grid:{left:43,right:17,top:26,bottom:41},textStyle:{fontFamily:'Microsoft YaHei, sans-serif',fontSize:13}};
      if(this.trendTotal>0 && this.$refs.trendChart) {
        const chart=echarts.init(this.$refs.trendChart);
        chart.setOption({...common,xAxis:{type:'category',data:this.trend.map(x=>x.name),axisLabel:{color:'#6d8774'}},yAxis:{type:'value',minInterval:1,axisLabel:{color:'#6d8774'}},series:[{type:'bar',barMaxWidth:36,data:this.trend.map(x=>x.value),itemStyle:{color:'#6a9977',borderRadius:[4,4,0,0]}}]});
        this.charts.push(chart);
      }
      if(this.categories.length>0 && this.$refs.categoryChart) {
        const chart=echarts.init(this.$refs.categoryChart);
        chart.setOption({...common,grid:{left:85,right:24,top:15,bottom:25},xAxis:{type:'value',minInterval:1,axisLabel:{color:'#6d8774'}},yAxis:{type:'category',inverse:true,data:this.categories.map(x=>x.name),axisLabel:{color:'#506f5b',fontSize:13}},series:[{type:'bar',barMaxWidth:23,data:this.categories.map(x=>x.value),itemStyle:{color:'#c6a86f',borderRadius:[0,4,4,0]}}]});
        this.charts.push(chart);
      }
    }
  }
};
</script>
<style scoped>
.home-hero{position:relative;overflow:hidden;min-height:340px;border-radius:16px;background:#234737;color:white;display:flex;align-items:center}.hero-copy{padding:48px 55px;position:relative;z-index:2;width:67%}.eyebrow{font-size:13px;letter-spacing:1px;color:#d0e1cd}.home-hero h1{font-size:38px;line-height:1.45;letter-spacing:1px;margin:18px 0 12px}.home-hero h1 em{font-style:normal;color:#e2c68c}.home-hero p{color:#c7dccb;font-size:15px;line-height:1.8;margin-bottom:25px}.home-hero small{font-size:12px;color:#c2d7c1;display:block;margin-top:12px}.search-box{background:white;border-radius:9px;max-width:600px;height:57px;display:flex;align-items:center;color:#718676;padding-left:18px;box-shadow:0 7px 24px rgba(14,45,26,.12)}.search-box>i{font-size:19px}.search-box input{border:0;outline:0;flex:1;min-width:0;font-size:15px;padding:0 13px;color:#244b39}.search-box button{align-self:stretch;border:0;border-radius:0 9px 9px 0;padding:0 22px;background:#e4c68f;color:#244938;font-weight:700;cursor:pointer;font-size:14px}.search-box button i{margin-left:8px}.hero-art{position:absolute;right:0;height:100%;width:41%;opacity:.9}.section-heading{display:flex;justify-content:space-between;align-items:flex-end;gap:20px;margin:0 0 20px}.section-heading h2{font-size:25px;color:#284534;margin:4px 0}.section-heading p{margin:0;color:#7e9282;font-size:14px}.section-kicker{color:#8ba18f;font-size:13px;font-weight:650}.numbers-section,.charts-section{margin-top:38px}.refresh{border:0;background:none;color:#477052;cursor:pointer;font-size:14px}.number-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:16px;min-height:145px}.number-card{border:1px solid #e3e9e1;background:#fff;border-radius:13px;padding:22px 24px;position:relative}.number-card .number-symbol{color:#638f6e;font-size:23px}.number-card p{font-size:14px;color:#738979;margin:14px 0 5px}.number-card strong{font-size:34px;line-height:1.2;color:#284b36;font-weight:750}.number-card span{margin-left:7px;font-size:13px;color:#8c9d8f}.number-card>b{position:absolute;top:24px;right:20px;color:#d1dbcf;font-weight:500}.chart-grid{display:grid;grid-template-columns:1fr 1fr;gap:18px}.chart-card,.notice-card,.guide-card{background:#fff;border:1px solid #e5eae2;border-radius:14px;padding:26px;min-height:255px}.card-heading{display:flex;align-items:center;justify-content:space-between;gap:12px}.card-heading h3,.guide-card h3{font-size:20px;margin:3px 0 7px;color:#284737}.card-heading>span{font-size:13px;color:#8da290}.chart-body{height:250px;width:100%}.empty-chart{height:250px;display:flex;justify-content:center;align-items:center;color:#9caf9c;background:repeating-linear-gradient(0deg,#fff,#fff 48px,#f1f4ef 49px);font-size:15px}.bottom-grid{display:grid;grid-template-columns:1.4fr 1fr;gap:18px;margin-top:21px}.notice-card .card-heading a{font-size:14px;color:#3f7551;text-decoration:none}.notice-empty{padding:50px 0;color:#9daea0;text-align:center;font-size:15px}.notice-item{display:flex;gap:11px;padding:15px 0;border-bottom:1px solid #eff2eb;text-decoration:none;align-items:center;color:#3c5942}.notice-item:last-child{border-bottom:0}.notice-dot{background:#85a993;width:7px;height:7px;border-radius:50%}.notice-text{display:flex;flex:1;flex-direction:column;gap:4px;min-width:0}.notice-text b{font-size:15px;font-weight:600;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}.notice-text small{font-size:12px;color:#91a495}.guide-card{position:relative;overflow:hidden;background:#f0f3e9}.guide-card p{max-width:420px;line-height:1.9;font-size:15px;color:#718775}.guide-card a{color:#2d6843;font-weight:700;text-decoration:none;font-size:14px}.guide-stamp{position:absolute;right:-18px;bottom:-22px;color:#dae5d3;font-size:140px;opacity:.7}.guide-stamp i{position:relative;z-index:0}.feedback{padding:12px 17px;margin:14px 0;background:#fff5f1;color:#955f52;border-radius:7px;font-size:14px}.feedback button{margin-left:12px;background:none;border:0;color:#945749;cursor:pointer;text-decoration:underline}@media(max-width:1080px){.hero-copy{width:76%}.hero-art{width:38%;opacity:.45}.number-grid{grid-template-columns:repeat(2,1fr)}}@media(max-width:760px){.home-hero{min-height:380px}.hero-copy{width:100%;padding:34px 25px}.home-hero h1{font-size:29px}.hero-art{opacity:.22;width:85%}.search-box{height:55px}.search-box button{padding:0 14px}.chart-grid,.bottom-grid{grid-template-columns:1fr}.section-heading{align-items:flex-start;flex-direction:column}.section-heading h2{font-size:23px}}@media(max-width:480px){.number-grid{grid-template-columns:1fr 1fr;gap:10px}.number-card{padding:19px 16px}.number-card strong{font-size:28px}.number-card span{display:block;margin:5px 0 0}.home-hero h1{font-size:27px}.search-box button{font-size:12px;padding:0 11px}}
</style>

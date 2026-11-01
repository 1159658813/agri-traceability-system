<template>
  <div class="search-view">
    <div class="search-header">
      <div class="search-icon"><i class="el-icon-search"></i></div>
      <h1>农产品溯源查询</h1>
      <p>消费者无需登录。仅展示已经审核发布、当前有效的批次资料。</p>
      <form @submit.prevent="submit">
        <el-input v-model.trim="code" maxlength="80" placeholder="请输入溯源码" clearable />
        <el-button type="primary" native-type="submit" :loading="loading">查询</el-button>
      </form>
      <p class="search-note">溯源资料为平台登记信息，不代表独立质量认证；系统含演示测试数据。</p>
    </div>

    <div v-if="errorMessage" class="status-message" role="alert">{{ errorMessage }}</div>

    <div v-if="result" class="result" aria-live="polite">
      <div v-if="result.demo" class="demo-notice">【演示数据】本批次为开发测试记录，不代表真实生产、检测或认证结果。</div>
      <div class="result-heading">
        <div>
          <span class="eyebrow">已发布的批次资料</span>
          <h2>{{ result.productName || '未命名产品' }}</h2>
          <span class="subtle">{{ result.categoryName || '农产品' }}<span v-if="result.variety"> · {{ result.variety }}</span></span>
        </div>
        <span class="published-label">已发布</span>
      </div>
      <div class="info-grid">
        <div class="info-item"><span>溯源码</span><strong>{{ result.traceCode }}</strong></div>
        <div class="info-item"><span>批次编号</span><strong>{{ result.batchNo }}</strong></div>
        <div class="info-item"><span>种植基地</span><strong>{{ result.baseName }}</strong></div>
        <div class="info-item"><span>产地</span><strong>{{ region }}</strong></div>
        <div class="info-item"><span>种植日期</span><strong>{{ dateText(result.plantingDate) }}</strong></div>
        <div class="info-item"><span>预计采收</span><strong>{{ dateText(result.plannedHarvestDate) }}</strong></div>
        <div class="info-item"><span>预计产量</span><strong>{{ result.estimatedQuantityKg != null ? result.estimatedQuantityKg + ' kg' : '未登记' }}</strong></div>
        <div class="info-item"><span>发布日期</span><strong>{{ dateText(result.publishedAt) }}</strong></div>
      </div>

      <div class="timeline-section">
        <h3>生产与流通记录</h3>
        <p class="section-note">按事件时间排列，质检结论仅为登记结果，不代表报告已获独立核验。</p>
        <div v-if="!result.events || result.events.length === 0" class="no-events">该批次尚无可公开展示的生产或流通记录。</div>
        <ol v-else class="timeline">
          <li v-for="(event, index) in result.events" :key="index" class="timeline-item">
            <span class="timeline-dot"></span>
            <div class="event-content">
              <div class="event-top">
                <span class="event-stage">{{ stageName(event.stage) }}</span>
                <time>{{ dateText(event.occurredAt) }}</time>
              </div>
              <div class="event-title">{{ event.title || '过程记录' }}</div>
              <p v-if="event.detail" class="event-detail">{{ event.detail }}</p>
              <div v-if="event.stage === 'QUALITY'" class="quality-note">
                登记结论：<strong>{{ qualityName(event.conclusion) }}</strong>（仅为资料登记，不代表认证）
              </div>
            </div>
          </li>
        </ol>
        <p v-if="result.moreEvents" class="section-note">记录较多，目前仅展示最近 200 条事件。</p>
      </div>
      <div class="privacy-note">为保护隐私，页面不公开农户联系方式、私人证明材料、内部审核意见和物流单号。</div>
    </div>
    <div class="return"><router-link to="/">返回平台首页</router-link></div>
  </div>
</template>

<script>
import { getPublicTrace } from '../../api/publicTrace';

export default {
  name: 'PublicSearch',
  data() {
    const linkedCode = typeof this.$route.query.code === 'string' ? this.$route.query.code : '';
    return { code: linkedCode, loading: false, errorMessage: '', result: null, requestId: 0 };
  },
  computed: {
    region() {
      if (!this.result) return '';
      return [this.result.province, this.result.city, this.result.district].filter(Boolean).join(' · ') || '未登记';
    }
  },
  mounted() {
    if (this.code) this.submit();
  },
  watch: {
    '$route.query.code'(value) {
      if (typeof value === 'string' && value !== this.code) {
        this.code = value;
        this.submit();
      }
    }
  },
  beforeDestroy() {
    this.requestId++;
  },
  methods: {
    async submit() {
      const current = ++this.requestId;
      this.result = null;
      this.errorMessage = '';
      const normalized = (this.code || '').trim();
      if (!/^[A-Za-z0-9_-]{4,80}$/.test(normalized)) {
        this.loading = false;
        this.errorMessage = '请输入 4～80 位字母、数字、连字符或下划线组成的溯源码。';
        return;
      }
      this.loading = true;
      try {
        const data = await getPublicTrace(normalized);
        if (current === this.requestId) this.result = data;
      } catch (error) {
        if (current === this.requestId) this.errorMessage = error.message || '查询失败';
      } finally {
        if (current === this.requestId) this.loading = false;
      }
    },
    dateText(value) {
      if (!value) return '未登记';
      const text = String(value);
      return text.replace('T', ' ').slice(0, 16);
    },
    stageName(value) {
      return { FARM: '农事', HARVEST: '采收', QUALITY: '质检', CIRCULATION: '流通' }[value] || '记录';
    },
    qualityName(value) {
      return { PENDING: '待检测', PASS: '通过（仅登记）', FAIL: '不通过' }[value] || '未说明';
    }
  }
};
</script>

<style scoped>
.search-view{max-width:960px;margin:38px auto 58px;padding:0 18px;color:#264a39}
.search-header{text-align:center;background:#fff;border:1px solid #e4ebe2;border-radius:16px;padding:44px 32px}
.search-icon{font-size:30px;color:#4b805e}
.search-header h1{font-size:29px;margin:12px 0;color:#294d39}
.search-header p{font-size:15px;color:#7c9581;line-height:1.8;margin:6px 0}
.search-header form{display:flex;gap:12px;max-width:560px;margin:28px auto 16px}
.search-header form .el-input{flex:1}
.search-header .search-note{font-size:12px;color:#94896f}
.status-message{margin-top:18px;background:#f7f0e4;color:#8e7148;text-align:left;padding:16px 19px;line-height:1.65;font-size:14px;border-radius:8px}
.result{background:#fff;border:1px solid #e4ebe2;border-radius:16px;padding:32px;margin-top:22px}
.demo-notice{background:#fbf2e0;color:#986a31;padding:13px 16px;border-radius:8px;margin-bottom:23px;line-height:1.6}
.result-heading{display:flex;align-items:center;justify-content:space-between;gap:14px;padding-bottom:21px;border-bottom:1px solid #e8ede6}
.result-heading h2{font-size:26px;margin:7px 0;color:#1e4936}
.eyebrow{font-size:12px;letter-spacing:.06em;color:#799383}
.subtle{font-size:13px;color:#728a7c}
.published-label{background:#e7f0e7;color:#317148;border-radius:100px;padding:6px 13px;font-size:13px;white-space:nowrap}
.info-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:15px 30px;padding:24px 0}
.info-item{display:flex;flex-direction:column;gap:5px;min-width:0;font-size:14px;overflow-wrap:anywhere}
.info-item span{color:#7a9283;font-size:12px}
.info-item strong{font-weight:500;color:#2b4e3d}
.timeline-section{border-top:1px solid #e8ede6;padding-top:20px}
.timeline-section h3{font-size:19px;color:#264a39;margin:0 0 7px}
.section-note{font-size:13px;color:#829389;line-height:1.6}
.timeline{list-style:none;margin:24px 0 0;padding:0 0 0 12px;border-left:2px solid #d3e2d4}
.timeline-item{position:relative;margin:0 0 15px 21px}
.timeline-dot{position:absolute;left:-39px;top:17px;width:12px;height:12px;border-radius:50%;background:#427456;border:2px solid white;box-shadow:0 0 0 1px #c4d9cb}
.event-content{border:1px solid #e6eee5;border-radius:11px;padding:15px 17px;background:#fdfefd}
.event-top{display:flex;justify-content:space-between;gap:10px;align-items:center;color:#799385;font-size:12px}
.event-stage{background:#edf4ed;color:#346446;border-radius:6px;padding:3px 8px}
.event-title{margin-top:9px;font-weight:600;color:#294c39;overflow-wrap:anywhere}
.event-detail{font-size:13px;color:#607c6a;margin:7px 0 0}
.quality-note{font-size:13px;color:#977341;margin-top:8px;line-height:1.55}
.no-events{margin:18px 0;border:1px dashed #dce6da;padding:24px;text-align:center;color:#819486;border-radius:10px}
.privacy-note{background:#f5f7f3;padding:13px 16px;color:#819286;font-size:12px;border-radius:8px;line-height:1.7;margin-top:20px}
.return{text-align:center;margin-top:25px}.return a{color:#3c7351;font-size:14px}
@media(max-width:650px){.search-header{padding:32px 17px}.search-header form{flex-direction:column}.result{padding:22px 16px}.info-grid{grid-template-columns:1fr}.result-heading h2{font-size:22px}}
</style>

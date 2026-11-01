<template>
  <section class="completeness-panel">
    <div class="heading">
      <div>
        <h2>资料完整性预览</h2>
        <p>仅依据记录是否存在计算，不代表质量合格或具备发布资格。</p>
      </div>
      <button type="button" :disabled="loading" @click="load">{{ loading ? '读取中...' : '刷新评分' }}</button>
    </div>
    <p v-if="error" class="error" role="alert">{{ error }}</p>
    <p v-else-if="loading && !report" class="subtle">正在读取批次资料...</p>
    <template v-if="report">
      <div class="score-row">
        <div><strong>{{ report.score }}</strong><span> / {{ report.maxScore }} 分</span></div>
        <span class="subtle">批次状态：{{ statusText(report.batchStatus) }}</span>
      </div>
      <div class="progress" role="progressbar" :aria-valuenow="report.score"
           aria-valuemin="0" :aria-valuemax="report.maxScore" aria-label="资料完整性评分">
        <div class="progress-fill" :style="{ width: `${report.score}%` }"></div>
      </div>
      <ul class="items">
        <li v-for="item in report.items" :key="item.code">
          <span>{{ item.label }}</span>
          <span :class="item.filled ? 'filled' : 'missing'">
            {{ item.filled ? '已登记' : '待完善' }} · {{ item.filled ? item.weight : 0 }}/{{ item.weight }} 分
          </span>
        </li>
      </ul>
      <div v-if="report.missingItems.length" class="missing-hints">
        <strong>待完善资料</strong>
        <p v-for="(item, index) in report.missingItems" :key="index">{{ item }}</p>
      </div>
      <p class="quality-notice">{{ report.qualityNotice }}</p>
      <p class="subtle disclaimer">{{ report.disclaimer }}</p>
    </template>
  </section>
</template>

<script>
import { getBatchCompleteness } from '../../api/farmerCompleteness';

export default {
  name: 'FarmerCompletenessPanel',
  props: { batchId: { type: [String, Number], required: true } },
  data() {
    return { report: null, loading: false, error: '' };
  },
  watch: { batchId() { this.load(); } },
  mounted() { this.load(); },
  methods: {
    statusText(status) {
      return { DRAFT: '草稿', SUBMITTED: '待审核', REJECTED: '已驳回', PUBLISHED: '已发布' }[status] || status;
    },
    async load() {
      this.loading = true;
      this.error = '';
      this.report = null;
      try {
        this.report = await getBatchCompleteness(this.batchId);
      } catch (error) {
        this.error = error.message || '评分读取失败';
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.completeness-panel { margin-top:20px; padding:24px; background:#fff; border:1px solid #e0e8df; border-radius:13px; color:#234a3c; }
.heading,.score-row,.items li { display:flex; justify-content:space-between; align-items:center; gap:16px; }
.heading h2 { margin:0; font-size:21px; }
.heading p,.subtle { font-size:13px; color:#819483; }
.heading button { border:1px solid #d0dfd2; border-radius:9px; background:white; color:#315943; padding:9px 13px; cursor:pointer; }
.heading button:disabled { opacity:.6; cursor:default; }
.score-row { margin-top:22px; }
.score-row strong { font-size:34px; }
.score-row span { font-size:13px; }
.progress { margin:12px 0 20px; background:#ebf0ea; border-radius:10px; height:10px; overflow:hidden; }
.progress-fill { height:100%; background:#326749; transition:width .2s; }
.items { list-style:none; padding:0; margin:0; display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:10px; }
.items li { padding:12px; border:1px solid #e5ebe3; border-radius:8px; font-size:14px; }
.filled { color:#36734d; }.missing { color:#936b33; }
.missing-hints { margin-top:16px; background:#f7f1e3; padding:14px; border-radius:8px; font-size:13px; color:#806b46; }
.missing-hints p { margin:7px 0 0; }
.quality-notice { margin:16px 0 0; color:#976b3d; font-size:13px; }
.disclaimer { margin-top:8px; line-height:1.7; }
.error { padding:10px; background:#fcefeb; color:#a53b32; border-radius:8px; }
@media(max-width:700px) { .heading,.score-row { align-items:flex-start; flex-direction:column; }.items { grid-template-columns:1fr; } }
</style>

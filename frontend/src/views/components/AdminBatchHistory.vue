<template>
  <section class="history-section">
    <button type="button" class="history-toggle" :aria-expanded="String(open)" @click="toggle">
      {{ open ? '收起审核与评分历史' : '查看审核与评分历史' }}
    </button>
    <div v-if="open" class="history-content">
      <p v-if="loading" class="history-muted">正在读取历史记录...</p>
      <p v-else-if="error" class="history-error" role="alert">{{ error }}</p>
      <template v-else-if="history">
        <p class="history-muted">审核日志和评分快照分别保存，按各自时间显示，不能简单认定它们逐条对应。</p>
        <h3>审核日志 <small>共 {{ history.auditTotal }} 条</small></h3>
        <p v-if="!history.audits.length" class="history-muted">暂无审核日志。</p>
        <ol v-else class="history-list">
          <li v-for="audit in history.audits" :key="audit.id">
            <div class="history-heading">
              <strong>{{ audit.decision === 'APPROVE' ? '演示发布' : audit.decision === 'REJECT' ? '驳回' : audit.decision }}</strong>
              <time>{{ formatTime(audit.reviewedAt) }}</time>
            </div>
            <p class="history-muted">审核人：{{ audit.reviewerName || '已登记管理员' }}</p>
            <p v-if="audit.comment" class="history-comment">审核意见：{{ audit.comment }}</p>
          </li>
        </ol>
        <p v-if="history.auditTotal > history.audits.length" class="history-muted">仅显示最近 100 条审核日志。</p>

        <h3>资料完整性评分快照 <small>共 {{ history.snapshotTotal }} 条</small></h3>
        <p v-if="!history.snapshots.length" class="history-muted">暂无历史评分；仅查看实时预览不会生成快照。</p>
        <ol v-else class="history-list">
          <li v-for="snapshot in history.snapshots" :key="snapshot.id">
            <div class="history-heading">
              <strong>{{ formatScore(snapshot.score) }} / 100 分</strong>
              <time>{{ formatTime(snapshot.evaluatedAt) }}</time>
            </div>
            <p class="history-muted">规则版本：{{ snapshot.ruleVersion }}</p>
            <template v-if="missingItems(snapshot).length">
              <p class="missing-title">当时缺失的资料：</p>
              <ul class="missing-items">
                <li v-for="(message, index) in missingItems(snapshot)" :key="index">{{ message }}</li>
              </ul>
            </template>
            <p v-else class="history-muted">该快照没有记录缺失项。</p>
          </li>
        </ol>
        <p v-if="history.snapshotTotal > history.snapshots.length" class="history-muted">仅显示最近 100 条评分历史。</p>
        <p class="history-muted disclaimer">历史评分只反映资料登记情况，不代表质量认证或报告真实性核验。</p>
      </template>
    </div>
  </section>
</template>

<script>
import { getAdminBatchHistory } from '../../api/adminBatchHistory';

export default {
  name: 'AdminBatchHistory',
  props: {
    batchId: { type: Number, required: true }
  },
  data() {
    return { open: false, loading: false, error: '', history: null, requestSerial: 0 };
  },
  beforeDestroy() { this.requestSerial += 1; },
  methods: {
    formatTime(value) { return value ? String(value).replace('T', ' ').slice(0, 19) : '—'; },
    formatScore(value) {
      const number = Number(value);
      return Number.isFinite(number) ? number.toFixed(2) : '—';
    },
    missingItems(snapshot) {
      try {
        const items = JSON.parse(snapshot.missingItemsJson || '[]');
        if (!Array.isArray(items)) return [];
        return items.slice(0, 100).map(item => {
          if (typeof item === 'string') return item;
          return item && typeof item === 'object' ? (item.message || item.ruleKey || '') : '';
        }).filter(Boolean);
      } catch (error) {
        return ['旧版缺失项格式无法解析'];
      }
    },
    async toggle() {
      if (this.open) {
        this.requestSerial += 1;
        this.open = false;
        return;
      }
      this.open = true;
      const serial = ++this.requestSerial;
      this.loading = true;
      this.error = '';
      try {
        const result = await getAdminBatchHistory(this.batchId);
        if (serial !== this.requestSerial) return;
        if (!result || Number(result.batchId) !== this.batchId) throw new Error('历史记录与当前批次不一致');
        this.history = result;
      } catch (error) {
        if (serial === this.requestSerial) this.error = error.message || '读取审核历史失败';
      } finally {
        if (serial === this.requestSerial) this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.history-section { margin-top: 18px; padding-top: 17px; border-top: 1px solid #e8ece5; }
.history-toggle { font: inherit; font-size: 13px; color: #244d3d; background: #fff; border: 1px solid #d5e2d7; border-radius: 9px; padding: 9px 14px; cursor: pointer; }
.history-content { margin-top: 13px; background: #fafbf8; border: 1px solid #e1e8df; border-radius: 11px; padding: 15px 18px; }
.history-content h3 { margin: 15px 0 9px; color: #244c3b; font-size: 16px; }
.history-content h3 small { font-size: 12px; font-weight: 400; color: #7b9584; }
.history-muted { color: #779085; font-size: 12px; line-height: 1.65; margin: 6px 0; }
.history-error { color: #a43e36; background: #fff0ed; padding: 12px; border-radius: 8px; }
.history-list { padding-left: 20px; margin: 7px 0 18px; }
.history-list > li { border-bottom: 1px solid #e6ebe4; padding: 12px 2px; }
.history-list > li:last-child { border-bottom: 0; }
.history-heading { display: flex; flex-wrap: wrap; justify-content: space-between; gap: 9px; color: #244c3b; }
.history-heading time { font-size: 12px; color: #779085; }
.history-comment { margin: 7px 0; color: #456858; white-space: pre-wrap; overflow-wrap: anywhere; font-size: 13px; }
.missing-title { font-size: 12px; margin: 8px 0 4px; color: #9b5947; }
.missing-items { margin: 4px 0 8px; padding-left: 18px; color: #9b5947; font-size: 12px; line-height: 1.7; }
.disclaimer { margin-top: 15px; border-top: 1px solid #e5eae2; padding-top: 12px; }
@media (max-width: 650px) { .history-content { padding: 12px; } }
</style>

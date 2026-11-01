<template>
  <section class="batch-review-page">
    <header class="page-header">
      <div>
        <p class="eyebrow">溯源业务 · 发布审核</p>
        <h1>批次审核</h1>
        <p>审核农户提交的溯源批次，记录审核人及审核决定。</p>
      </div>
      <button class="plain-button" type="button" @click="load">刷新列表</button>
    </header>

    <div class="notice">
      仅允许明确标记“【演示】”且通过资料完整性、质检状态校验的测试批次发布。
      演示发布不代表报告真实或产品质量合格；正式批次仍需独立的材料核验流程。
    </div>

    <div class="toolbar">
      <label for="batch-review-status">审核状态</label>
      <select id="batch-review-status" v-model="status" @change="changeStatus">
        <option value="SUBMITTED">待审核</option>
        <option value="PUBLISHED">已发布</option>
        <option value="REJECTED">已驳回</option>
        <option value="ALL">全部</option>
      </select>
      <span class="total">共 {{ total }} 条</span>
    </div>

    <p v-if="error" class="error" role="alert">{{ error }}</p>
    <div v-if="loading" class="empty">正在读取批次...</div>
    <div v-else-if="!records.length" class="empty">当前筛选条件下没有批次</div>
    <div v-else class="batch-list">
      <article v-for="item in records" :key="item.id" class="batch-card">
        <div class="card-head">
          <div>
            <span class="batch-no">{{ item.batchNo }}</span>
            <h2>{{ item.productName }}</h2>
            <p class="sub">{{ item.baseName }} · {{ item.displayName || item.username }}（{{ item.username }}）</p>
          </div>
          <span class="status-tag" :class="item.status.toLowerCase()">{{ statusName(item.status) }}</span>
        </div>
        <dl class="details">
          <div><dt>溯源码</dt><dd>{{ item.traceCode }}</dd></div>
          <div><dt>种植日期</dt><dd>{{ item.plantingDate || '未填写' }}</dd></div>
          <div><dt>预计采收</dt><dd>{{ item.plannedHarvestDate || '未填写' }}</dd></div>
          <div><dt>最后修改</dt><dd>{{ formatDate(item.updatedAt) }}</dd></div>
        </dl>
        <p class="remark" v-if="item.remark">批次备注：{{ item.remark }}</p>
        <p class="record-summary">
          关联记录：农事 {{ item.farmCount }} 条 · 采收 {{ item.harvestCount }} 条 ·
          质检 {{ item.qualityCount }} 条 · 流通 {{ item.circulationCount }} 条
        </p>
        <p class="fine-print">以上为录入条数，不代表材料经过核验。</p>

        <div class="completeness-controls">
          <button type="button" class="plain-button" :aria-expanded="reportBatchId === item.id"
                  @click="toggleCompleteness(item)">
            {{ reportBatchId === item.id ? '收起完整性评分' : '查看完整性评分' }}
          </button>
          <span v-if="reportBatchId === item.id && reportLoading" class="fine-print">正在计算评分...</span>
        </div>
        <section v-if="reportBatchId === item.id" class="completeness-report" aria-label="资料完整性评分">
          <p v-if="reportError" class="error" role="alert">{{ reportError }}</p>
          <template v-else-if="report && report.batchId === item.id">
            <div class="completeness-head">
              <div>
                <strong class="completeness-score">{{ report.score }}<small> / {{ report.maxScore }} 分</small></strong>
                <p class="fine-print">依据数据库当前启用的六项完整性规则计算</p>
              </div>
              <span class="review-eligibility" :class="{ eligible: report.readyForDemoReview }">
                {{ item.status !== 'SUBMITTED' ? '历史批次 · 仅供查看' :
                  (report.readyForDemoReview && item.demoEligible ? '满足当前演示审核条件' : '暂不满足演示发布条件') }}
              </span>
            </div>
            <div class="score-track" role="progressbar" :aria-valuenow="Number(report.score)"
                 :aria-valuemin="0" :aria-valuemax="Number(report.maxScore)" aria-label="完整性得分">
              <div class="score-fill" :style="{ width: scorePercent(report) + '%' }"></div>
            </div>
            <div class="completeness-rules">
              <div v-for="rule in report.rules" :key="rule.key" class="completeness-rule">
                <span>{{ rule.name }}</span>
                <span :class="rule.filled ? 'rule-filled' : 'rule-missing'">
                  {{ rule.filled ? '已登记' : '待补全' }} · {{ rule.filled ? rule.weight : 0 }}/{{ rule.weight }} 分
                </span>
              </div>
            </div>
            <div v-if="report.missingItems && report.missingItems.length" class="report-alert">
              <strong>缺失资料</strong>
              <ul><li v-for="(missing, index) in report.missingItems" :key="index">{{ missing }}</li></ul>
            </div>
            <div v-if="report.warnings && report.warnings.length" class="report-warning">
              <strong>资料与质检提示</strong>
              <ul><li v-for="(warning, index) in report.warnings" :key="index">{{ warning }}</li></ul>
            </div>
            <p class="fine-print">规则版本：{{ report.ruleVersion }}。评分不是检测认证，也不替代管理员审核。</p>
          </template>
          <p v-else-if="reportLoading" class="fine-print">正在读取数据库评分...</p>
        </section>

        <div v-if="item.status === 'SUBMITTED'" class="review-actions">
          <template v-if="editingId === item.id">
            <label :for="`batch-comment-${item.id}`">审核意见</label>
            <textarea :id="`batch-comment-${item.id}`" v-model="comment" maxlength="450"
                      rows="3" placeholder="驳回时必须说明原因"></textarea>
            <label v-if="item.demoEligible" class="confirm-demo">
              <input v-model="demoConfirmed" type="checkbox" />
              我确认这是演示批次，仅用于系统开发测试，不代表真实质检认证
            </label>
            <p v-else class="fine-print">非演示批次当前仅支持驳回，不能正式发布。</p>
            <p v-if="item.demoEligible && (!report || !report.readyForDemoReview)" class="fine-print">
              必须先获取并通过完整性与质检校验，才能进行演示发布；资料不符合要求时仍可驳回。
            </p>
            <div class="buttons">
              <button v-if="item.demoEligible" type="button" class="approve"
                      :disabled="saving || !demoConfirmed || reportLoading ||
                                 reportBatchId !== item.id || !report || !report.readyForDemoReview"
                      @click="submit(item, 'APPROVE')">
                演示发布
              </button>
              <button type="button" class="reject" :disabled="saving"
                      @click="submit(item, 'REJECT')">驳回</button>
              <button type="button" class="plain-button" :disabled="saving"
                      @click="editingId = null">取消</button>
            </div>
          </template>
          <button v-else type="button" class="plain-button" @click="begin(item)">审核此批次</button>
        </div>
      </article>
    </div>

    <div class="pager">
      <button class="plain-button" type="button" :disabled="loading || pageNum <= 1"
              @click="go(pageNum - 1)">上一页</button>
      <span>第 {{ pageNum }} / {{ pages }} 页</span>
      <button class="plain-button" type="button" :disabled="loading || pageNum >= pages"
              @click="go(pageNum + 1)">下一页</button>
    </div>
  </section>
</template>

<script>
import { currentUser } from '../api/auth';
import { getAdminBatches, getAdminBatchCompleteness, reviewBatch } from '../api/adminBatches';

export default {
  name: 'AdminBatchReview',
  data() {
    return {
      status: 'SUBMITTED',
      pageNum: 1,
      pageSize: 10,
      total: 0,
      records: [],
      loading: false,
      saving: false,
      editingId: null,
      comment: '',
      demoConfirmed: false,
      reportBatchId: null,
      report: null,
      reportLoading: false,
      reportError: '',
      reportSerial: 0,
      error: ''
    };
  },
  computed: {
    pages() { return Math.max(1, Math.ceil(this.total / this.pageSize)); }
  },
  mounted() { this.load(); },
  methods: {
    statusName(value) {
      return { SUBMITTED: '待审核', PUBLISHED: '已发布', REJECTED: '已驳回' }[value] || value;
    },
    formatDate(value) { return value ? String(value).replace('T', ' ').slice(0, 16) : '—'; },
    changeStatus() {
      this.pageNum = 1;
      this.editingId = null;
      this.load();
    },
    go(page) {
      this.pageNum = page;
      this.editingId = null;
      this.load();
    },
    async load() {
      this.resetReport();
      this.loading = true;
      this.error = '';
      try {
        const data = await getAdminBatches(this.status, this.pageNum, this.pageSize);
        this.records = data.records || [];
        this.total = data.total || 0;
      } catch (error) {
        this.error = error.message || '读取审核列表失败';
      } finally {
        this.loading = false;
      }
    },
    begin(item) {
      this.editingId = item.id;
      this.comment = '';
      this.demoConfirmed = false;
      this.error = '';
      this.openCompleteness(item);
    },
    scorePercent(report) {
      const max = Number(report.maxScore) || 100;
      const value = Number(report.score) || 0;
      return Math.max(0, Math.min(100, value / max * 100));
    },
    resetReport() {
      this.reportSerial += 1;
      this.reportBatchId = null;
      this.report = null;
      this.reportLoading = false;
      this.reportError = '';
    },
    toggleCompleteness(item) {
      if (this.reportBatchId === item.id) {
        this.resetReport();
        return;
      }
      this.openCompleteness(item);
    },
    async openCompleteness(item) {
      const serial = ++this.reportSerial;
      this.reportBatchId = item.id;
      this.report = null;
      this.reportError = '';
      this.reportLoading = true;
      try {
        const result = await getAdminBatchCompleteness(item.id);
        if (this.reportSerial !== serial) return;
        if (!result || result.batchId !== item.id) throw new Error('评分结果与当前批次不一致');
        this.report = result;
      } catch (error) {
        if (this.reportSerial === serial) this.reportError = error.message || '读取评分失败';
      } finally {
        if (this.reportSerial === serial) this.reportLoading = false;
      }
    },
    async submit(item, decision) {
      if (this.saving) return;
      if (decision === 'REJECT' && !this.comment.trim()) {
        this.error = '驳回时必须填写审核意见';
        return;
      }
      if (decision === 'APPROVE' && (!item.demoEligible || !this.demoConfirmed)) {
        this.error = '请确认本次仅发布演示批次';
        return;
      }
      if (decision === 'APPROVE' && (this.reportBatchId !== item.id ||
          !this.report || !this.report.readyForDemoReview || this.reportLoading)) {
        this.error = '当前完整性或质检状态不满足演示发布条件，请刷新评分';
        return;
      }
      const prompt = decision === 'APPROVE'
        ? '确认将该演示批次发布到公众查询与统计中？此操作不能撤销。'
        : '确认驳回该批次并要求农户修改？';
      if (!window.confirm(prompt)) return;
      this.saving = true;
      this.error = '';
      try {
        const account = await currentUser();
        if (account.roleCode !== 'ADMIN') throw new Error('当前账号没有管理员权限');
        await reviewBatch(item.id, decision, this.comment.trim(), account.csrfToken);
        this.editingId = null;
        this.comment = '';
        this.demoConfirmed = false;
        await this.load();
      } catch (error) {
        this.error = error.message || '操作失败';
      } finally {
        this.saving = false;
      }
    }
  }
};
</script>

<style scoped>
.batch-review-page { max-width: 1200px; margin: 0 auto; padding: 36px 28px 64px; color: #24473a; }
.page-header { display: flex; justify-content: space-between; align-items: start; gap: 24px; }
.eyebrow { color: #809b86; font-size: 13px; margin: 0 0 10px; }
h1 { font-size: 27px; margin: 0 0 10px; }
h2 { margin: 6px 0; font-size: 21px; }
p { line-height: 1.65; }
.page-header p:not(.eyebrow), .sub, .record-summary, .fine-print { color: #748e82; }
.notice { padding: 16px; background: #f5f1e4; border: 1px solid #e9dfc3; border-radius: 10px; line-height: 1.75; margin: 18px 0 22px; }
.toolbar, .pager, .buttons { display: flex; align-items: center; gap: 12px; }
.toolbar { margin-bottom: 18px; }
.total { margin-left: auto; color: #748e82; }
select, textarea, button { font: inherit; }
select, textarea { border: 1px solid #d8e3d9; border-radius: 8px; background: #fff; color: #234739; padding: 10px 12px; }
textarea { width: 100%; box-sizing: border-box; resize: vertical; margin: 9px 0; }
button { cursor: pointer; border-radius: 9px; padding: 9px 14px; }
button:disabled { cursor: not-allowed; opacity: .55; }
.plain-button { border: 1px solid #d5e2d7; background: #fff; color: #244d3d; }
.batch-list { display: grid; gap: 17px; }
.batch-card { border: 1px solid #e1e8df; border-radius: 14px; background: #fff; padding: 22px; }
.card-head { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; border-bottom: 1px solid #edf0ea; padding-bottom: 15px; }
.batch-no { font-size: 13px; color: #829a8a; }
.sub { margin: 7px 0 0; font-size: 14px; }
.status-tag { display: inline-block; padding: 5px 12px; border-radius: 999px; white-space: nowrap; font-size: 13px; background: #f1e7c9; color: #876427; }
.status-tag.published { background: #e3efe6; color: #386f50; }
.status-tag.rejected { background: #f9e8e4; color: #964b40; }
.details { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 15px; margin: 20px 0 14px; }
dt { font-size: 13px; color: #819a8d; margin-bottom: 8px; }
dd { margin: 0; overflow-wrap: anywhere; }
.remark { background: #f7f8f4; border-radius: 8px; padding: 10px 13px; }
.record-summary { margin-bottom: 0; }
.fine-print { font-size: 12px; margin-top: 4px; }
/* 管理员完整性报告：沿用既有森林绿与暖白色视觉 */
.completeness-controls { display: flex; align-items: center; gap: 12px; margin-top: 17px; }
.completeness-report { margin-top: 12px; padding: 17px; border: 1px solid #dfebdf; border-radius: 11px; background: #fafbf8; }
.completeness-head { display: flex; justify-content: space-between; align-items: center; gap: 16px; }
.completeness-score { color: #244d3d; font-size: 28px; }
.completeness-score small { font-size: 14px; font-weight: normal; }
.review-eligibility { font-size: 12px; color: #96642d; background: #f5eee1; border-radius: 8px; padding: 7px 10px; }
.review-eligibility.eligible { color: #286246; background: #e5f1e8; }
.score-track { height: 9px; margin: 12px 0 16px; border-radius: 99px; background: #e6ede6; overflow: hidden; }
.score-fill { height: 100%; background: #356b4f; border-radius: inherit; transition: width .2s; }
.completeness-rules { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 9px; }
.completeness-rule { display: flex; justify-content: space-between; align-items: center; gap: 10px; padding: 10px; border: 1px solid #e4ebe3; border-radius: 7px; background: #fff; font-size: 13px; }
.rule-filled { color: #27704a; white-space: nowrap; }
.rule-missing { color: #a05c3d; white-space: nowrap; }
.report-alert, .report-warning { margin-top: 13px; padding: 12px 14px; border-radius: 8px; font-size: 13px; line-height: 1.7; }
.report-alert { background: #fdf0ec; color: #a0463d; }
.report-warning { background: #f7f1e5; color: #84602e; }
.report-alert ul, .report-warning ul { margin: 6px 0 0; padding-left: 20px; }
.review-actions { border-top: 1px solid #e8ece5; margin-top: 18px; padding-top: 17px; }
.review-actions label:not(.confirm-demo) { display: block; }
.confirm-demo { display: flex; align-items: flex-start; gap: 8px; line-height: 1.7; margin-bottom: 14px; }
.confirm-demo input { margin-top: 6px; }
.approve { background: #244c3b; border: 1px solid #244c3b; color: white; }
.reject { background: #fff; border: 1px solid #be867c; color: #975448; }
.error { color: #a43e36; background: #fff0ed; padding: 12px; border-radius: 8px; }
.empty { text-align: center; padding: 75px 14px; color: #80978b; background: #fff; border: 1px solid #e1e8df; border-radius: 13px; }
.pager { justify-content: flex-end; margin-top: 18px; color: #678273; }
@media (max-width: 750px) { .batch-review-page { padding: 20px 14px; } .page-header, .completeness-head { flex-direction: column; align-items: flex-start; } .details, .completeness-rules { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 520px) { .completeness-rules { grid-template-columns: 1fr; } }
</style>

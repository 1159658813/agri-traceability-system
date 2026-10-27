<template>
  <section class="review-page">
    <header class="review-header">
      <div>
        <p class="eyebrow">农户入驻 · 账号审核</p>
        <h1>入驻申请审核</h1>
        <p class="description">查看申请状态并测试审核流程。管理员可查看演示申请及新提交的申请。</p>
      </div>
      <button class="refresh-button" type="button" @click="load">刷新列表</button>
    </header>

    <div class="notice-bar">演示记录没有真实证明材料；新提交的文件需人工核验，系统不会自动判断证明材料真伪。</div>

    <div class="toolbar">
      <label for="review-status">申请状态</label>
      <select id="review-status" v-model="status" @change="changeStatus">
        <option value="PENDING">待审核</option>
        <option value="APPROVED">已通过</option>
        <option value="REJECTED">已驳回</option>
        <option value="ALL">全部申请</option>
      </select>
      <span class="total">共 {{ total }} 条</span>
    </div>

    <p v-if="error" class="error" role="alert">{{ error }}</p>
    <div v-if="loading" class="empty">正在读取申请记录...</div>
    <div v-else-if="!records.length" class="empty">当前筛选条件下没有申请记录</div>

    <div v-else class="application-list">
      <article v-for="item in records" :key="item.id" class="application-card">
        <div class="application-title">
          <div>
            <h2>{{ item.organizationName || item.contactName }}</h2>
            <span class="sub-title">申请编号 #{{ item.id }} · {{ item.username }}</span>
          </div>
          <span class="status" :class="item.reviewStatus.toLowerCase()">
            {{ statusName(item.reviewStatus) }}
          </span>
        </div>
        <dl class="information">
          <div><dt>主体类型</dt><dd>{{ item.applicantType === 'COOPERATIVE' ? '合作社' : '个人农户' }}</dd></div>
          <div><dt>联系人</dt><dd>{{ item.contactName }}</dd></div>
          <div><dt>联系电话</dt><dd>{{ item.contactPhone }}</dd></div>
          <div><dt>种植地区</dt><dd>{{ item.farmingLocation }}</dd></div>
          <div><dt>提交时间</dt><dd>{{ formatDate(item.submittedAt) }}</dd></div>
          <div><dt>资质材料</dt><dd><span v-if="item.demoProof">演示占位记录（无真实文件）</span>
            <a v-else :href="`/api/admin/farmer-applications/${item.id}/proof`" class="proof-link">下载资料（管理员）</a></dd></div>
        </dl>
        <p v-if="item.applicationNote" class="application-note">申请说明：{{ item.applicationNote }}</p>
        <p v-if="item.reviewComment" class="application-note">审核意见：{{ item.reviewComment }}</p>

        <div v-if="item.reviewStatus === 'PENDING'" class="review-actions">
          <template v-if="editingId === item.id">
            <label :for="`comment-${item.id}`">审核意见</label>
            <textarea :id="`comment-${item.id}`" v-model="comment" maxlength="500"
                      rows="3" placeholder="驳回申请时必须填写原因"></textarea>
            <label v-if="!item.demoProof" class="proof-confirm">
              <input v-model="proofChecked" type="checkbox" /> 我已核对附件内容并确认可以进行人工审核
            </label>
            <div class="action-buttons">
              <button type="button" class="approve" :disabled="saving" @click="submit(item, 'APPROVED')">{{ saving ? '处理中...' : '通过申请' }}</button>
              <button type="button" class="reject" :disabled="saving" @click="submit(item, 'REJECTED')">驳回申请</button>
              <button type="button" class="cancel" :disabled="saving" @click="editingId = null">取消</button>
            </div>
          </template>
          <button v-else type="button" class="review-button" @click="startReview(item)">处理这条申请</button>
        </div>
      </article>
    </div>

    <div class="pager">
      <button type="button" :disabled="loading || pageNum <= 1" @click="toPage(pageNum - 1)">上一页</button>
      <span>第 {{ pageNum }} / {{ pages }} 页</span>
      <button type="button" :disabled="loading || pageNum >= pages" @click="toPage(pageNum + 1)">下一页</button>
    </div>
  </section>
</template>

<script>
import { currentUser } from '../api/auth';
import { getApplications, reviewApplication } from '../api/farmerReview';

export default {
  name: 'FarmerReview',
  data() {
    return {
      status: 'PENDING',
      pageNum: 1,
      pageSize: 10,
      total: 0,
      records: [],
      loading: false,
      saving: false,
      error: '',
      editingId: null,
      comment: '',
      proofChecked: false
    };
  },
  computed: {
    pages() { return Math.max(1, Math.ceil(this.total / this.pageSize)); }
  },
  mounted() { this.load(); },
  methods: {
    statusName(value) {
      return { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }[value] || value;
    },
    formatDate(value) {
      return value ? String(value).replace('T', ' ').slice(0, 16) : '—';
    },
    changeStatus() {
      this.pageNum = 1;
      this.editingId = null;
      this.load();
    },
    toPage(value) {
      this.pageNum = value;
      this.load();
    },
    async load() {
      this.loading = true;
      this.error = '';
      try {
        const data = await getApplications(this.status, this.pageNum, this.pageSize);
        this.records = data.records || [];
        this.total = data.total || 0;
      } catch (error) {
        this.error = error.message || '无法读取申请记录';
      } finally {
        this.loading = false;
      }
    },
    startReview(item) {
      this.editingId = item.id;
      this.comment = '';
      this.proofChecked = false;
      this.error = '';
    },
    async submit(item, decision) {
      if (this.saving) return;
      if (!item.demoProof && !this.proofChecked) {
        this.error = '请先核对并确认已查看证明材料';
        return;
      }
      if (decision === 'REJECTED' && !this.comment.trim()) {
        this.error = '请先填写驳回原因';
        return;
      }
      if (!window.confirm(`确定${decision === 'APPROVED' ? '通过' : '驳回'}这条申请吗？`)) return;
      this.saving = true;
      this.error = '';
      try {
        const account = await currentUser();
        if (account.roleCode !== 'ADMIN') throw new Error('当前账号没有管理员权限');
        await reviewApplication(item.id, decision, this.comment.trim(), account.csrfToken);
        this.editingId = null;
        this.comment = '';
        await this.load();
      } catch (error) {
        this.error = error.message || '审核失败';
      } finally {
        this.saving = false;
      }
    }
  }
};
</script>

<style scoped>
.review-page { padding: 28px 32px 48px; color: #234a3c; background: #f8f9f5; min-height: 100%; }
.review-header { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; }
.eyebrow { color: #78967d; font-size: 13px; margin: 0 0 7px; }
h1 { font-size: 26px; margin: 0 0 8px; }
.description { margin: 0 0 20px; color: #718675; font-size: 14px; }
button, select, textarea { font: inherit; }
button { cursor: pointer; }
.refresh-button, .review-button, .cancel, .pager button { padding: 9px 14px; border: 1px solid #ceded1; background: #fff; color: #315b45; border-radius: 8px; }
.notice-bar { background: #f2f1e8; color: #6b6442; border: 1px solid #e7e3c9; border-radius: 9px; padding: 12px 16px; font-size: 13px; }
.toolbar { display: flex; gap: 12px; align-items: center; padding: 18px 0; font-size: 14px; }
.toolbar select { border: 1px solid #d7e2d8; padding: 8px 12px; border-radius: 8px; background: #fff; color: #234a3c; }
.total { margin-left: auto; color: #819485; }
.application-list { display: grid; gap: 14px; }
.application-card { background: #fff; border: 1px solid #e3ebe1; border-radius: 13px; padding: 20px 23px; }
.application-title { display: flex; justify-content: space-between; gap: 12px; align-items: flex-start; border-bottom: 1px solid #f0f3ee; padding-bottom: 13px; }
h2 { margin: 0 0 5px; font-size: 18px; }
.sub-title { font-size: 12px; color: #879b8d; }
.status { font-size: 12px; border-radius: 99px; padding: 5px 11px; background: #f3ebcf; color: #856c36; white-space: nowrap; }
.status.approved { background: #e5f2e7; color: #31704a; }
.status.rejected { background: #faeaea; color: #a44c45; }
.information { display: grid; grid-template-columns: repeat(3, minmax(0,1fr)); gap: 14px 20px; margin: 17px 0; }
.information div { min-width: 0; }
dt { color: #93a293; font-size: 12px; margin-bottom: 5px; }
dd { margin: 0; color: #405e4c; font-size: 14px; overflow-wrap: anywhere; }
.application-note { font-size: 13px; color: #6f8275; background: #f8faf7; border-radius: 6px; padding: 9px 12px; }
.review-actions { border-top: 1px solid #f0f3ee; padding-top: 16px; }
.review-actions label { display: block; font-size: 13px; margin-bottom: 7px; }
textarea { width: 100%; box-sizing: border-box; border: 1px solid #d6e2d7; border-radius: 8px; padding: 11px; resize: vertical; }
.action-buttons { display: flex; gap: 10px; margin-top: 12px; }
.approve, .reject { border: none; border-radius: 8px; padding: 10px 18px; color: #fff; }
.approve { background: #2f6650; }.reject { background: #a65e52; }
.hint { font-size: 12px; color: #a08454; }
.error { color: #a44c45; background: #fff0ef; padding: 10px 15px; border-radius: 8px; }
.empty { background: #fff; border: 1px solid #e3ebe1; border-radius: 12px; padding: 62px 20px; text-align: center; color: #91a292; }
.pager { display: flex; justify-content: flex-end; align-items: center; gap: 12px; font-size: 13px; margin-top: 17px; }
button:disabled { opacity: .5; cursor: not-allowed; }
@media (max-width: 800px) { .review-page { padding: 18px 14px; } .information { grid-template-columns: repeat(2, minmax(0,1fr)); } }
@media (max-width: 520px) { .review-header { flex-direction: column; } .information { grid-template-columns: 1fr; } }
.proof-link { color: #32644c; text-decoration: underline; }
.proof-confirm { display: flex !important; align-items: center; gap: 8px; font-size: 13px; color: #4d654e; margin: 8px 0; }
.proof-confirm input { width: auto; }
</style>


<template>
  <div class="extra-records">
    <p v-if="error" class="alert" role="alert">{{ error }}</p>
    <div v-if="loading" class="empty">正在读取质检与流通记录...</div>
    <template v-else>
      <section class="record-section">
        <div class="section-heading">
          <div>
            <h2>质量检测记录</h2>
            <p>检测结论是资料登记，不等于平台认证或报告真实性核验。</p>
          </div>
          <button v-if="editable" class="primary" type="button" @click="openQuality()">新增质检记录</button>
        </div>
        <p class="notice">无真实检测报告时请保留“待检测”状态，不虚构第三方机构或报告号。演示 PASS/FAIL 记录须在备注中写明【演示】。</p>
        <div v-if="!qualityRecords.length" class="empty">暂无质量检测记录</div>
        <div v-else class="record-list">
          <article v-for="item in qualityRecords" :key="item.id" class="record-card">
            <div class="card-top">
              <strong>{{ item.inspectionItem }}</strong>
              <span>{{ displayTime(item.inspectedAt) }}</span>
            </div>
            <p class="record-description">检测状态：<strong :class="['result', item.conclusion.toLowerCase()]">{{ conclusionText(item.conclusion) }}</strong></p>
            <p v-if="item.inspectionOrg">检测机构：{{ item.inspectionOrg }}</p>
            <p v-if="item.reportNo">报告编号：{{ item.reportNo }}</p>
            <p v-if="item.remark" class="record-description">{{ item.remark }}</p>
            <div class="record-footer">
              <span>质检记录 #{{ item.id }}</span>
              <button v-if="editable" type="button" @click="openQuality(item)">编辑</button>
            </div>
          </article>
        </div>
      </section>

      <section class="record-section">
        <div class="section-heading">
          <div>
            <h2>流通事件时间线</h2>
            <p>按事件发生时间从早到晚展示，支持补录历史节点。</p>
          </div>
          <button v-if="editable" class="primary" type="button" @click="openCirculation()">新增流通事件</button>
        </div>
        <div v-if="!circulationRecords.length" class="empty">暂无流通事件</div>
        <ol v-else class="timeline">
          <li v-for="item in circulationRecords" :key="item.id" class="timeline-entry">
            <div class="timeline-card">
              <div class="card-top"><strong>{{ item.eventType }}</strong><span>{{ displayTime(item.eventAt) }}</span></div>
              <p v-if="item.fromPlace || item.toPlace" class="record-description">{{ item.fromPlace || '未填写始发地' }} → {{ item.toPlace || '未填写到达地' }}</p>
              <p v-if="item.operatorName">经办人：{{ item.operatorName }}</p>
              <p v-if="item.logisticsNo">物流单号：{{ item.logisticsNo }}</p>
              <p v-if="item.remark" class="record-description">{{ item.remark }}</p>
              <div class="record-footer">
                <span>流通记录 #{{ item.id }}</span>
                <button v-if="editable" type="button" @click="openCirculation(item)">编辑</button>
              </div>
            </div>
          </li>
        </ol>
      </section>
    </template>

    <div v-if="dialog" class="overlay" @click.self="closeDialog">
      <div class="dialog" role="dialog" aria-modal="true" aria-labelledby="extra-record-title">
        <div class="dialog-heading">
          <h2 id="extra-record-title">{{ dialogTitle }}</h2>
          <button type="button" :disabled="saving" @click="closeDialog">关闭</button>
        </div>
        <form v-if="dialog === 'quality'" @submit.prevent="saveQuality">
          <label>检测时间
            <input v-model="qualityForm.inspectedAt" type="datetime-local" required />
          </label>
          <label>检测项目
            <input v-model.trim="qualityForm.inspectionItem" maxlength="150" required placeholder="如：【演示】外观检查" />
          </label>
          <label>检测机构
            <input v-model.trim="qualityForm.inspectionOrg" maxlength="150" placeholder="选填；无真实机构请留空" />
          </label>
          <label>报告编号
            <input v-model.trim="qualityForm.reportNo" maxlength="100" placeholder="选填；无真实报告请留空" />
          </label>
          <label>检测结论
            <select v-model="qualityForm.conclusion" required>
              <option value="PENDING">待检测（默认）</option>
              <option value="PASS">通过（仅限标注演示的测试数据）</option>
              <option value="FAIL">不通过（仅限标注演示的测试数据）</option>
            </select>
          </label>
          <label>检测备注
            <textarea v-model.trim="qualityForm.remark" maxlength="500" rows="3"
                      placeholder="测试 PASS/FAIL 必须包含【演示】，不得宣称实际检测" />
          </label>
          <p v-if="formError" class="alert" role="alert">{{ formError }}</p>
          <button class="primary submit" type="submit" :disabled="saving">{{ saving ? '保存中...' : '保存质检记录' }}</button>
        </form>
        <form v-else @submit.prevent="saveCirculation">
          <label>流通事件类型
            <select v-model="circulationForm.eventType" required>
              <option value="" disabled>请选择事件类型</option>
              <option v-for="type in eventTypes" :key="type" :value="type">{{ type }}</option>
            </select>
          </label>
          <label>事件时间
            <input v-model="circulationForm.eventAt" type="datetime-local" required />
          </label>
          <label>始发地
            <input v-model.trim="circulationForm.fromPlace" maxlength="150" placeholder="选填" />
          </label>
          <label>到达地
            <input v-model.trim="circulationForm.toPlace" maxlength="150" placeholder="选填" />
          </label>
          <label>经办人
            <input v-model.trim="circulationForm.operatorName" maxlength="80" placeholder="选填" />
          </label>
          <label>物流单号
            <input v-model.trim="circulationForm.logisticsNo" maxlength="100" placeholder="选填；没有真实单号请留空" />
          </label>
          <label>事件备注
            <textarea v-model.trim="circulationForm.remark" maxlength="500" rows="3" placeholder="演示记录请注明【演示】" />
          </label>
          <p v-if="formError" class="alert" role="alert">{{ formError }}</p>
          <button class="primary submit" type="submit" :disabled="saving">{{ saving ? '保存中...' : '保存流通事件' }}</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getSupplementalRecords, addQuality, editQuality, addCirculation, editCirculation
} from '../../api/farmerSupplementalRecords';

const emptyQuality = () => ({
  inspectedAt: '', inspectionItem: '', inspectionOrg: '', reportNo: '', conclusion: 'PENDING', remark: ''
});
const emptyCirculation = () => ({
  eventType: '', eventAt: '', fromPlace: '', toPlace: '', operatorName: '', logisticsNo: '', remark: ''
});

export default {
  name: 'QualityCirculationRecords',
  props: {
    batchId: { type: [String, Number], required: true },
    editable: { type: Boolean, required: true }
  },
  data() {
    return {
      qualityRecords: [], circulationRecords: [], loading: true, saving: false,
      error: '', formError: '', dialog: '', editId: null,
      qualityForm: emptyQuality(), circulationForm: emptyCirculation(),
      eventTypes: ['包装', '出库', '运输', '到达', '签收', '其他']
    };
  },
  computed: {
    dialogTitle() {
      const type = this.dialog === 'quality' ? '质检记录' : '流通事件';
      return `${this.editId ? '编辑' : '新增'}${type}`;
    }
  },
  watch: {
    batchId() { this.load(); },
    editable(value) { if (!value) this.dialog = ''; }
  },
  mounted() { this.load(); },
  methods: {
    displayTime(value) { return value ? value.replace('T', ' ').slice(0, 16) : '—'; },
    formTime(value) { return value ? value.slice(0, 16) : ''; },
    conclusionText(value) {
      return { PENDING: '待检测', PASS: '通过（仅作记录）', FAIL: '不通过（仅作记录）' }[value] || value;
    },
    async load() {
      this.loading = true;
      this.error = '';
      try {
        const data = await getSupplementalRecords(this.batchId);
        this.qualityRecords = data.qualityRecords || [];
        this.circulationRecords = data.circulationRecords || [];
      } catch (error) {
        this.error = error.message || '读取质检与流通记录失败';
      } finally {
        this.loading = false;
      }
    },
    openQuality(item) {
      if (!this.editable) return;
      this.editId = item ? item.id : null;
      this.qualityForm = item ? {
        inspectedAt: this.formTime(item.inspectedAt), inspectionItem: item.inspectionItem,
        inspectionOrg: item.inspectionOrg || '', reportNo: item.reportNo || '',
        conclusion: item.conclusion, remark: item.remark || ''
      } : emptyQuality();
      this.formError = '';
      this.dialog = 'quality';
    },
    openCirculation(item) {
      if (!this.editable) return;
      this.editId = item ? item.id : null;
      this.circulationForm = item ? {
        eventType: item.eventType, eventAt: this.formTime(item.eventAt),
        fromPlace: item.fromPlace || '', toPlace: item.toPlace || '',
        operatorName: item.operatorName || '', logisticsNo: item.logisticsNo || '',
        remark: item.remark || ''
      } : emptyCirculation();
      this.formError = '';
      this.dialog = 'circulation';
    },
    closeDialog() { if (!this.saving) this.dialog = ''; },
    async saveQuality() {
      if (this.saving) return;
      this.saving = true;
      this.formError = '';
      try {
        if (!this.qualityForm.inspectedAt || !this.qualityForm.inspectionItem.trim()) {
          throw new Error('请填写检测时间与检测项目');
        }
        if (this.qualityForm.conclusion !== 'PENDING' &&
            !this.qualityForm.remark.includes('【演示】')) {
          throw new Error('当前 PASS/FAIL 仅用于演示，备注必须包含【演示】');
        }
        if (this.editId) await editQuality(this.batchId, this.editId, this.qualityForm);
        else await addQuality(this.batchId, this.qualityForm);
        this.dialog = '';
        await this.load();
      } catch (error) {
        this.formError = error.message || '质检记录保存失败';
      } finally {
        this.saving = false;
      }
    },
    async saveCirculation() {
      if (this.saving) return;
      this.saving = true;
      this.formError = '';
      try {
        if (!this.circulationForm.eventType || !this.circulationForm.eventAt) {
          throw new Error('请选择流通事件类型及发生时间');
        }
        if (this.editId) await editCirculation(this.batchId, this.editId, this.circulationForm);
        else await addCirculation(this.batchId, this.circulationForm);
        this.dialog = '';
        await this.load();
      } catch (error) {
        this.formError = error.message || '流通事件保存失败';
      } finally {
        this.saving = false;
      }
    }
  }
};
</script>

<style scoped>
.extra-records { color:#234a3c; }
.record-section { background:#fff; border:1px solid #e0e8df; border-radius:13px; margin-top:20px; padding:24px; }
.section-heading,.card-top,.record-footer,.dialog-heading { display:flex; align-items:center; justify-content:space-between; gap:14px; }
.section-heading h2 { font-size:21px; margin:0; }
.section-heading p { color:#819483; font-size:14px; margin:4px 0; }
button { background:#fff; color:#315943; border:1px solid #d0dfd2; border-radius:9px; font:inherit; cursor:pointer; padding:10px 15px; }
button:disabled { opacity:.6; cursor:not-allowed; }
.primary { color:#fff; background:#234a3c; border-color:#234a3c; }
.notice { color:#806b46; background:#f7f1e3; border:1px solid #ece2c5; border-radius:8px; padding:12px 15px; font-size:13px; }
.alert { padding:13px; color:#a53b32; background:#fcefeb; border-radius:8px; }
.empty { color:#829684; padding:34px 12px; text-align:center; }
.record-list { display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:14px; margin-top:22px; }
.record-card,.timeline-card { border:1px solid #e5ebe3; border-radius:9px; padding:20px; }
.card-top span,.record-footer { color:#819483; font-size:13px; }
.record-card p,.timeline-card p { font-size:14px; color:#466554; line-height:1.7; }
.record-description { white-space:pre-wrap; }
.result.pending { color:#8a692d; }.result.pass { color:#36734d; }.result.fail { color:#a53b32; }
.record-footer { border-top:1px solid #eef2ed; padding-top:12px; margin-top:17px; }
.record-footer button { padding:6px 12px; font-size:12px; }
.timeline { list-style:none; padding:0 0 0 21px; margin:22px 0 0; border-left:2px solid #d4e3d6; }
.timeline-entry { position:relative; margin-bottom:16px; padding-left:15px; }
.timeline-entry::before { content:''; position:absolute; left:-29px; top:17px; width:11px; height:11px; border-radius:50%; background:#36674f; border:3px solid #f6faf6; }
.overlay { position:fixed; inset:0; z-index:30; background:rgba(23,49,37,.5); display:flex; align-items:center; justify-content:center; padding:20px; }
.dialog { width:min(580px,100%); max-height:calc(100vh - 40px); overflow:auto; background:#fff; border-radius:14px; padding:28px; box-sizing:border-box; }
.dialog-heading { margin-bottom:24px; }.dialog-heading h2 { margin:0; }
label { display:block; font-size:14px; margin:18px 0; }
input,select,textarea { display:block; box-sizing:border-box; width:100%; font:inherit; margin-top:9px; padding:11px 13px; border:1px solid #cbdccb; border-radius:8px; color:#254a3d; background:#fff; }
textarea { resize:vertical; }.submit { width:100%; margin-top:10px; }
@media (max-width:700px) { .section-heading { align-items:flex-start; flex-direction:column; }.record-list { grid-template-columns:1fr; }.record-section { padding:18px; } }
</style>

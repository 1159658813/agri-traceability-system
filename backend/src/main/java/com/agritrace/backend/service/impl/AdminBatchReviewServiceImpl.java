package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.AdminBatchReviewMapper;
import com.agritrace.backend.service.AdminBatchReviewService;
import com.agritrace.backend.service.BatchReviewProblem;
import com.agritrace.backend.vo.admin.BatchReviewLock;
import com.agritrace.backend.vo.admin.BatchReviewPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.agritrace.backend.service.CompletenessReviewService;
import com.agritrace.backend.vo.admin.CompletenessReviewReport;


@Service
public class AdminBatchReviewServiceImpl implements AdminBatchReviewService {
    private final AdminBatchReviewMapper mapper;
    private final CompletenessReviewService completeness;


    public AdminBatchReviewServiceImpl(AdminBatchReviewMapper mapper,CompletenessReviewService completeness) {
        this.mapper = mapper;
        this.completeness = completeness;
    }

    @Override
    @Transactional(readOnly = true)
    public BatchReviewPage list(String status, int pageNum, int pageSize) {
        if (!("ALL".equals(status) || "SUBMITTED".equals(status)
                || "PUBLISHED".equals(status) || "REJECTED".equals(status))) {
            throw new BatchReviewProblem(400, "筛选状态不正确");
        }
        if (pageNum < 1 || pageNum > 100000 || pageSize < 1 || pageSize > 50) {
            throw new BatchReviewProblem(400, "分页参数不正确");
        }
        long offset = ((long) pageNum - 1) * pageSize;
        return new BatchReviewPage(mapper.count(status), pageNum, pageSize,
                mapper.list(status, pageSize, offset));
    }

    @Override
    @Transactional
    public void review(Long id, Long reviewerId, String decision, String comment) {
        if (id == null || id < 1 || reviewerId == null) {
            throw new BatchReviewProblem(400, "批次编号不正确");
        }
        if (!("APPROVE".equals(decision) || "REJECT".equals(decision))) {
            throw new BatchReviewProblem(400, "审核决定不正确");
        }
        String note = comment == null ? "" : comment.trim();
        if (note.length() > 450 || ("REJECT".equals(decision) && note.isBlank())) {
            throw new BatchReviewProblem(400, "驳回必须填写原因，审核意见最多450字");
        }
        BatchReviewLock batch = mapper.lockBatch(id);
        if (batch == null) {
            throw new BatchReviewProblem(404, "批次不存在");
        }
        if (!"SUBMITTED".equals(batch.getStatus())) {
            throw new BatchReviewProblem(409, "批次状态已变化，请刷新页面");
        }
        CompletenessReviewReport report = completeness.forAdmin(id);
        int updated;
        if ("APPROVE".equals(decision)) {
            if (mapper.canPublishDemo(id) != 1) {
                throw new BatchReviewProblem(409,
                        "当前仅允许发布明确标记的演示批次，真实批次需等待过程记录与质检审核完善");
            }
            if (!report.readyForDemoReview()) {
                throw new BatchReviewProblem(409, "演示批次资料不完整或质检状态不符合发布条件，请查看评分详情");
            }
            updated = mapper.publish(id);
            note = "【演示测试】非真实农产品合格认证。" + note;
        } else {
            updated = mapper.reject(id);
        }
        if (updated != 1 || mapper.writeAudit(id, reviewerId, decision, note) != 1) {
            throw new BatchReviewProblem(409, "审核未完成，状态已回滚，请重试");
        }
        completeness.saveSnapshot(report);
    }
}

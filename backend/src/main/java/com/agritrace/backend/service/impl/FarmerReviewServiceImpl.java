package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.FarmerReviewMapper;
import com.agritrace.backend.dto.farmer.ApplicationLock;
import com.agritrace.backend.dto.farmer.FarmerAccountLock;
import com.agritrace.backend.service.FarmerReviewService;
import com.agritrace.backend.service.ReviewProblem;
import com.agritrace.backend.vo.farmer.ApplicationPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FarmerReviewServiceImpl implements FarmerReviewService {
    private final FarmerReviewMapper mapper;

    public FarmerReviewServiceImpl(FarmerReviewMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationPage findPage(String status, int pageNum, int pageSize) {
        if (!("ALL".equals(status) || "PENDING".equals(status)
                || "APPROVED".equals(status) || "REJECTED".equals(status))) {
            throw new ReviewProblem(400, "申请状态不正确");
        }
        if (pageNum < 1 || pageNum > 100000 || pageSize < 1 || pageSize > 50) {
            throw new ReviewProblem(400, "分页参数不正确");
        }
        int offset = (pageNum - 1) * pageSize;
        return new ApplicationPage(mapper.findPage(status, pageSize, offset),
                mapper.countByStatus(status), pageNum, pageSize);
    }

    @Override
    @Transactional
    public void review(Long applicationId, Long reviewerId, String decision, String comment) {
        if (applicationId == null || applicationId <= 0 || reviewerId == null) {
            throw new ReviewProblem(400, "申请编号不正确");
        }
        if (!("APPROVED".equals(decision) || "REJECTED".equals(decision))) {
            throw new ReviewProblem(400, "审核决定不正确");
        }
        String note = comment == null ? "" : comment.trim();
        if (note.length() > 500 || ("REJECTED".equals(decision) && note.isEmpty())) {
            throw new ReviewProblem(400, "驳回时必须填写审核意见，最多500字");
        }
        ApplicationLock application = mapper.lockApplication(applicationId);
        if (application == null) {
            throw new ReviewProblem(404, "申请记录不存在");
        }
        if (!"PENDING".equals(application.getReviewStatus())) {
            throw new ReviewProblem(409, "这条申请已经处理，请刷新列表");
        }
        // 真实证照上传、核验尚未上线；本阶段只允许审核明确标记的演示记录。
        if (!"DEMO_PLACEHOLDER".equals(application.getProofType())
                || application.getProofFilePath() == null
                || !application.getProofFilePath().startsWith("DEMO_ONLY/NO_REAL_FILE/")) {
            throw new ReviewProblem(409, "真实资质材料审核尚未开放，目前仅支持演示申请");
        }
        FarmerAccountLock farmer = mapper.lockFarmer(application.getApplicantId());
        if (farmer == null || !"FARMER".equals(farmer.getRoleCode())
                || !Integer.valueOf(0).equals(farmer.getStatus())) {
            throw new ReviewProblem(409, "申请账号状态不允许审核，请检查用户信息");
        }
        if (mapper.updateReview(applicationId, decision, reviewerId, note) != 1) {
            throw new ReviewProblem(409, "审核状态已发生变化，请刷新列表");
        }
        if ("APPROVED".equals(decision) && mapper.activateFarmer(application.getApplicantId()) != 1) {
            throw new ReviewProblem(409, "农户账户启用失败，本次审核已撤销");
        }
    }
}

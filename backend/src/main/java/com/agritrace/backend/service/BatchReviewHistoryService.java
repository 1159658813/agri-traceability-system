package com.agritrace.backend.service;

import com.agritrace.backend.dao.BatchReviewHistoryMapper;
import com.agritrace.backend.vo.admin.BatchReviewHistory;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchReviewHistoryService {
    private final BatchReviewHistoryMapper mapper;

    public BatchReviewHistoryService(BatchReviewHistoryMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public BatchReviewHistory get(Long batchId) {
        if (batchId == null || batchId < 1) {
            throw new BatchReviewProblem(400, "批次编号不正确");
        }
        if (mapper.batchExists(batchId) != 1) {
            throw new BatchReviewProblem(404, "批次不存在");
        }
        long audits = mapper.auditCount(batchId);
        long snapshots = mapper.snapshotCount(batchId);
        return new BatchReviewHistory(batchId, audits, snapshots,
                audits == 0 ? List.of() : mapper.listAudits(batchId),
                snapshots == 0 ? List.of() : mapper.listSnapshots(batchId));
    }
}

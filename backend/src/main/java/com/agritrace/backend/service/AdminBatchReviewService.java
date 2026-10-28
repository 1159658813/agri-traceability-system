package com.agritrace.backend.service;

import com.agritrace.backend.vo.admin.BatchReviewPage;

public interface AdminBatchReviewService {
    BatchReviewPage list(String status, int pageNum, int pageSize);

    void review(Long id, Long reviewerId, String decision, String comment);
}

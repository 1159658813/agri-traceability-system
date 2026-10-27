package com.agritrace.backend.service;

import com.agritrace.backend.vo.farmer.ApplicationPage;

public interface FarmerReviewService {
    ApplicationPage findPage(String status, int pageNum, int pageSize);

    void review(Long applicationId, Long reviewerId, String decision, String comment);
}

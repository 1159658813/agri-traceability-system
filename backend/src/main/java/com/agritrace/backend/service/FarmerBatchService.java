package com.agritrace.backend.service;

import com.agritrace.backend.dto.farmer.FarmerBatchForm;
import com.agritrace.backend.vo.farmer.FarmerBatchOptions;
import com.agritrace.backend.vo.farmer.FarmerBatchPage;
import com.agritrace.backend.vo.farmer.FarmerBatchRow;

public interface FarmerBatchService {
    FarmerBatchOptions options(Long farmerId);
    FarmerBatchPage list(Long farmerId, int pageNum, int pageSize);
    FarmerBatchRow find(Long farmerId, Long id);
    void create(Long farmerId, FarmerBatchForm form);
    void update(Long farmerId, Long id, FarmerBatchForm form);
    void submit(Long farmerId, Long id);
}

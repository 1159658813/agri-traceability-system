package com.agritrace.backend.service;

import com.agritrace.backend.dto.farmer.FarmWorkForm;
import com.agritrace.backend.dto.farmer.HarvestForm;
import com.agritrace.backend.vo.farmer.ProductionRecords;

public interface FarmerProductionRecordService {
    ProductionRecords list(Long farmerId, Long batchId);
    void createFarm(Long farmerId, Long batchId, FarmWorkForm form);
    void updateFarm(Long farmerId, Long batchId, Long recordId, FarmWorkForm form);
    void createHarvest(Long farmerId, Long batchId, HarvestForm form);
    void updateHarvest(Long farmerId, Long batchId, Long recordId, HarvestForm form);
}

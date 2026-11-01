package com.agritrace.backend.service;

import com.agritrace.backend.dto.farmer.CirculationForm;
import com.agritrace.backend.dto.farmer.QualityInspectionForm;
import com.agritrace.backend.vo.farmer.SupplementalRecords;

public interface FarmerSupplementalRecordService {
    SupplementalRecords list(Long farmerId, Long batchId);
    void createQuality(Long farmerId, Long batchId, QualityInspectionForm form);
    void updateQuality(Long farmerId, Long batchId, Long recordId, QualityInspectionForm form);
    void createCirculation(Long farmerId, Long batchId, CirculationForm form);
    void updateCirculation(Long farmerId, Long batchId, Long recordId, CirculationForm form);
}

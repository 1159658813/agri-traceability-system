package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.FarmerProductionRecordMapper;
import com.agritrace.backend.dao.FarmerSupplementalRecordMapper;
import com.agritrace.backend.dto.farmer.CirculationForm;
import com.agritrace.backend.dto.farmer.QualityInspectionForm;
import com.agritrace.backend.service.FarmerRecordProblem;
import com.agritrace.backend.vo.farmer.BatchRecordInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FarmerSupplementalRecordServiceImplTest {
    private FarmerProductionRecordMapper batchMapper;
    private FarmerSupplementalRecordMapper mapper;
    private FarmerSupplementalRecordServiceImpl service;
    private final Long ownerId = 7L;
    private final Long batchId = 19L;

    @BeforeEach
    void init() {
        batchMapper = mock(FarmerProductionRecordMapper.class);
        mapper = mock(FarmerSupplementalRecordMapper.class);
        service = new FarmerSupplementalRecordServiceImpl(batchMapper, mapper);
    }

    @Test
    void privateBatchCannotBeReadByDifferentFarmer() {
        FarmerRecordProblem error = assertThrows(FarmerRecordProblem.class,
                () -> service.list(ownerId, batchId));
        assertEquals(404, error.getCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void draftCanSavePendingQualityWithoutImaginaryReport() {
        when(batchMapper.lockBatch(ownerId, batchId)).thenReturn(batch("DRAFT"));
        when(mapper.createQuality(eq(batchId), any())).thenReturn(1);
        service.createQuality(ownerId, batchId, new QualityInspectionForm(
                LocalDateTime.now().minusDays(1), "【演示】外观检查", null, null, "PENDING", "【演示】未实际送检"));
        verify(mapper).createQuality(eq(batchId), any());
    }

    @Test
    void nonDemoPassResultIsRejected() {
        when(batchMapper.lockBatch(ownerId, batchId)).thenReturn(batch("DRAFT"));
        FarmerRecordProblem error = assertThrows(FarmerRecordProblem.class,
                () -> service.createQuality(ownerId, batchId, new QualityInspectionForm(
                        LocalDateTime.now().minusDays(1), "抽检", null, null, "PASS", "真实合格")));
        assertEquals(400, error.getCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void explicitlyMarkedDemoResultMayBeSaved() {
        when(batchMapper.lockBatch(ownerId, batchId)).thenReturn(batch("DRAFT"));
        when(mapper.createQuality(eq(batchId), any())).thenReturn(1);
        service.createQuality(ownerId, batchId, new QualityInspectionForm(
                LocalDateTime.now().minusDays(1), "【演示】实验项", null, null, "PASS", "【演示】模拟结果，不作为认证"));
        verify(mapper).createQuality(eq(batchId), any());
    }

    @Test
    void submittedBatchCannotAddCirculation() {
        when(batchMapper.lockBatch(ownerId, batchId)).thenReturn(batch("SUBMITTED"));
        FarmerRecordProblem error = assertThrows(FarmerRecordProblem.class,
                () -> service.createCirculation(ownerId, batchId, event()));
        assertEquals(409, error.getCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void crossBatchUpdateCannotModifyOtherRecords() {
        when(batchMapper.lockBatch(ownerId, batchId)).thenReturn(batch("DRAFT"));
        when(mapper.updateCirculation(eq(ownerId), eq(batchId), eq(100L), any())).thenReturn(0);
        FarmerRecordProblem error = assertThrows(FarmerRecordProblem.class,
                () -> service.updateCirculation(ownerId, batchId, 100L, event()));
        assertEquals(404, error.getCode());
    }

    @Test
    void circulationCannotPredatePlanting() {
        when(batchMapper.lockBatch(ownerId, batchId)).thenReturn(batch("DRAFT"));
        CirculationForm old = new CirculationForm("包装", LocalDateTime.of(2020, 1, 1, 9, 0),
                null, null, null, null, "【演示】");
        FarmerRecordProblem error = assertThrows(FarmerRecordProblem.class,
                () -> service.createCirculation(ownerId, batchId, old));
        assertEquals(400, error.getCode());
        verifyNoInteractions(mapper);
    }

    private BatchRecordInfo batch(String status) {
        BatchRecordInfo info = new BatchRecordInfo();
        info.setStatus(status);
        info.setPlantingDate(LocalDate.of(2026, 1, 1));
        return info;
    }

    private CirculationForm event() {
        return new CirculationForm("包装", LocalDateTime.now().minusDays(1),
                "【演示】产地", null, null, null, "【演示】模拟流通记录");
    }
}

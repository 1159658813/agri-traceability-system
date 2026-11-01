package com.agritrace.backend.service;

import com.agritrace.backend.vo.farmer.CompletenessFacts;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BatchCompletenessServiceTest {
    private final BatchCompletenessService service = new BatchCompletenessService(null);

    @Test
    void emptyBatchGetsZeroPointsAndSixMissingItems() {
        var report = service.evaluate(new CompletenessFacts());
        assertEquals(0, report.score());
        assertEquals(100, report.maxScore());
        assertEquals(6, report.missingItems().size());
    }

    @Test
    void allSixSectionsGetOneHundredPoints() {
        var facts = fullFacts();
        var report = service.evaluate(facts);
        assertEquals(100, report.score());
        assertTrue(report.missingItems().isEmpty());
    }

    @Test
    void missingQualityAndCirculationDeductThirtyFive() {
        var facts = fullFacts();
        facts.setQualityCount(0);
        facts.setCirculationCount(0);
        var report = service.evaluate(facts);
        assertEquals(65, report.score());
        assertEquals(2, report.missingItems().size());
    }

    @Test
    void pendingQualityStillCountsAsRegisteredButShowsWarning() {
        var facts = fullFacts();
        facts.setPendingQualityCount(1);
        var report = service.evaluate(facts);
        assertEquals(100, report.score());
        assertTrue(report.qualityNotice().contains("待检测"));
    }

    @Test
    void failedQualityMustNotBeInterpretedAsPublishable() {
        var facts = fullFacts();
        facts.setFailedQualityCount(1);
        var report = service.evaluate(facts);
        assertEquals(100, report.score());
        assertTrue(report.qualityNotice().contains("不通过"));
        assertTrue(report.disclaimer().contains("尚未"));
    }

    @Test
    void invalidBatchIdIsRejectedBeforeQueryingDatabase() {
        var error = assertThrows(FarmerRecordProblem.class, () -> service.forFarmer(1L, -1L));
        assertEquals(400, error.getCode());
    }

    private CompletenessFacts fullFacts() {
        var facts = new CompletenessFacts();
        facts.setBatchStatus("DRAFT");
        facts.setBaseCount(1);
        facts.setProductCount(1);
        facts.setFarmCount(1);
        facts.setHarvestCount(1);
        facts.setQualityCount(1);
        facts.setCirculationCount(1);
        return facts;
    }
}

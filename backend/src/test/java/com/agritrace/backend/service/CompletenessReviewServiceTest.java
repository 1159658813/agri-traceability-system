package com.agritrace.backend.service;

import com.agritrace.backend.dao.CompletenessReviewMapper;
import com.agritrace.backend.vo.admin.CompletenessRuleConfig;
import com.agritrace.backend.vo.farmer.CompletenessFacts;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CompletenessReviewServiceTest {
    private final CompletenessReviewMapper mapper = mock(CompletenessReviewMapper.class);
    private final CompletenessReviewService service = new CompletenessReviewService(mapper, new ObjectMapper());

    @Test
    void fullDemoWithPassIsDataReadyButNotCertified() {
        var report = service.evaluate(2L, fullFacts(), rules());
        assertEquals(0, report.score().compareTo(new BigDecimal("100.00")));
        assertTrue(report.readyForDemoReview());
        assertTrue(report.warnings().stream().anyMatch(w -> w.contains("不代表")));
    }

    @Test
    void failureBlocksReleaseEvenWithHundredPoints() {
        var f = fullFacts(); f.setFailedQualityCount(1);
        var report = service.evaluate(2L, f, rules());
        assertEquals(0, report.score().compareTo(new BigDecimal("100")));
        assertFalse(report.readyForDemoReview());
    }

    @Test
    void pendingBlocksReleaseEvenWithHundredPoints() {
        var f = fullFacts(); f.setPendingQualityCount(1);
        assertFalse(service.evaluate(2L, f, rules()).readyForDemoReview());
    }

    @Test
    void noPassBlocksReleaseEvenWithHundredPoints() {
        var f = fullFacts(); f.setPassedQualityCount(0);
        assertFalse(service.evaluate(2L, f, rules()).readyForDemoReview());
    }

    @Test
    void absentFarmDeductsConfiguredWeight() {
        var f = fullFacts(); f.setFarmCount(0);
        var report = service.evaluate(2L, f, rules());
        assertEquals(0, report.score().compareTo(new BigDecimal("80")));
        assertEquals(1, report.missingItems().size());
        assertFalse(report.readyForDemoReview());
    }

    @Test
    void brokenRuleTotalFailsClosed() {
        var r = rules(); r.get(0).setWeight(new BigDecimal("14"));
        assertEquals(409, assertThrows(BatchReviewProblem.class,
                () -> service.evaluate(2L, fullFacts(), r)).getStatus());
    }

    @Test
    void disabledRuleSetFailsClosed() {
        var r = rules(); r.remove(0);
        assertThrows(BatchReviewProblem.class, () -> service.evaluate(2L, fullFacts(), r));
    }

    @Test
    void snapshotUsesJsonAndStableRuleVersion() {
        var report = service.evaluate(2L, fullFacts(), rules());
        assertEquals(report.ruleVersion(), service.evaluate(2L, fullFacts(), rules()).ruleVersion());
        when(mapper.insertSnapshot(eq(2L), eq(report.ruleVersion()), eq(report.score()), eq("[]")))
                .thenReturn(1);
        service.saveSnapshot(report);
        verify(mapper).insertSnapshot(eq(2L), eq(report.ruleVersion()), eq(report.score()), eq("[]"));
    }

    private CompletenessFacts fullFacts() {
        var f = new CompletenessFacts();
        f.setBatchStatus("SUBMITTED"); f.setBaseCount(1); f.setProductCount(1);
        f.setFarmCount(1); f.setHarvestCount(1); f.setQualityCount(1);
        f.setCirculationCount(1); f.setPassedQualityCount(1);
        return f;
    }

    private List<CompletenessRuleConfig> rules() {
        List<CompletenessRuleConfig> list = new ArrayList<>();
        String[] keys = {"BASE_INFO", "PRODUCT_INFO", "FARM_RECORD", "HARVEST_RECORD", "QUALITY_RECORD", "CIRCULATION_RECORD"};
        int[] weights = {15, 10, 20, 20, 20, 15};
        for (int i = 0; i < keys.length; i++) {
            var rule = new CompletenessRuleConfig();
            rule.setRuleKey(keys[i]); rule.setRuleName(keys[i]);
            rule.setWeight(BigDecimal.valueOf(weights[i])); rule.setRequired(true);
            list.add(rule);
        }
        return list;
    }
}

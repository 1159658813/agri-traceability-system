package com.agritrace.backend.vo.admin;

import java.math.BigDecimal;
import java.util.List;

public record CompletenessReviewReport(Long batchId, String batchStatus, String ruleVersion,
                                       BigDecimal score, BigDecimal maxScore,
                                       List<CompletenessReviewRule> rules,
                                       List<String> missingItems, List<String> warnings,
                                       boolean readyForDemoReview) {
}

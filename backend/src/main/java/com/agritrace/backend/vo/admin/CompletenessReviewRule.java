package com.agritrace.backend.vo.admin;

import java.math.BigDecimal;

public record CompletenessReviewRule(String key, String name, BigDecimal weight,
                                     boolean required, boolean filled, String missingHint) {
}

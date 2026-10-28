package com.agritrace.backend.dto.farmer;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FarmerBatchForm(
        Long productId,
        Long baseId,
        LocalDate plantingDate,
        LocalDate plannedHarvestDate,
        BigDecimal estimatedQuantityKg,
        String remark) {
}

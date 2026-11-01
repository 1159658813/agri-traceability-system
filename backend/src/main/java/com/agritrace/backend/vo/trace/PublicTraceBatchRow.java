package com.agritrace.backend.vo.trace;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Database projection: intentionally contains no account, proof or internal audit fields. */
public record PublicTraceBatchRow(
        Long id,
        String batchNo,
        String traceCode,
        String productName,
        String variety,
        String categoryName,
        String baseName,
        String province,
        String city,
        String district,
        LocalDate plantingDate,
        LocalDate plannedHarvestDate,
        BigDecimal estimatedQuantityKg,
        LocalDateTime publishedAt,
        boolean demo
) {
}

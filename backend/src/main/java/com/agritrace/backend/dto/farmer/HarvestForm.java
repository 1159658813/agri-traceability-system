package com.agritrace.backend.dto.farmer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HarvestForm(LocalDateTime harvestedAt, BigDecimal quantityKg,
                          String grade, String remark) {
}

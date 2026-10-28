package com.agritrace.backend.dto.farmer;

import java.time.LocalDateTime;

public record FarmWorkForm(String operationType, LocalDateTime operatedAt,
                           String description, String operatorName) {
}

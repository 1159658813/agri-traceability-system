package com.agritrace.backend.dto.farmer;

import java.time.LocalDateTime;

public record CirculationForm(String eventType, LocalDateTime eventAt, String fromPlace,
                              String toPlace, String operatorName,
                              String logisticsNo, String remark) {
}

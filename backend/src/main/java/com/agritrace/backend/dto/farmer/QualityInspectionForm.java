package com.agritrace.backend.dto.farmer;

import java.time.LocalDateTime;

public record QualityInspectionForm(LocalDateTime inspectedAt, String inspectionItem,
                                    String inspectionOrg, String reportNo,
                                    String conclusion, String remark) {
}

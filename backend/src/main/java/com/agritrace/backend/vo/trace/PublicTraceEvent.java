package com.agritrace.backend.vo.trace;

import java.time.LocalDateTime;

public record PublicTraceEvent(
        String stage,
        LocalDateTime occurredAt,
        String title,
        String detail,
        String conclusion
) {
}

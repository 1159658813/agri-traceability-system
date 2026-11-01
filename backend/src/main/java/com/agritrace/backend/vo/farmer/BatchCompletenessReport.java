package com.agritrace.backend.vo.farmer;

import java.util.List;

public record BatchCompletenessReport(
        String batchStatus,
        int score,
        int maxScore,
        List<CompletenessItem> items,
        List<String> missingItems,
        String qualityNotice,
        String disclaimer) {
}

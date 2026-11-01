package com.agritrace.backend.vo.admin;

import java.util.List;

public record BatchReviewHistory(
        Long batchId,
        long auditTotal,
        long snapshotTotal,
        List<BatchAuditHistoryEntry> audits,
        List<BatchScoreHistoryEntry> snapshots
) {
}

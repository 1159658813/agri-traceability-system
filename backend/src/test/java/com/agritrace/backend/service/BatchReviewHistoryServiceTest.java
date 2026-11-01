package com.agritrace.backend.service;

import com.agritrace.backend.dao.BatchReviewHistoryMapper;
import com.agritrace.backend.vo.admin.BatchAuditHistoryEntry;
import com.agritrace.backend.vo.admin.BatchScoreHistoryEntry;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BatchReviewHistoryServiceTest {
    private final BatchReviewHistoryMapper mapper = mock(BatchReviewHistoryMapper.class);
    private final BatchReviewHistoryService service = new BatchReviewHistoryService(mapper);

    @Test
    void rejectsInvalidId() {
        assertEquals(400, assertThrows(BatchReviewProblem.class, () -> service.get(0L)).getStatus());
        verifyNoInteractions(mapper);
    }

    @Test
    void unknownBatchReturns404() {
        assertEquals(404, assertThrows(BatchReviewProblem.class, () -> service.get(999L)).getStatus());
        verify(mapper).batchExists(999L);
        verify(mapper, never()).listAudits(anyLong());
    }

    @Test
    void emptyHistoryDoesNotFetchLists() {
        when(mapper.batchExists(3L)).thenReturn(1L);
        var result = service.get(3L);
        assertTrue(result.audits().isEmpty());
        assertTrue(result.snapshots().isEmpty());
        verify(mapper, never()).listSnapshots(anyLong());
    }

    @Test
    void keepsAuditAndSnapshotStreamsSeparate() {
        when(mapper.batchExists(4L)).thenReturn(1L);
        when(mapper.auditCount(4L)).thenReturn(2L);
        when(mapper.snapshotCount(4L)).thenReturn(1L);
        var audit = new BatchAuditHistoryEntry();
        audit.setDecision("REJECT");
        var snapshot = new BatchScoreHistoryEntry();
        snapshot.setRuleVersion("RULES-demo");
        when(mapper.listAudits(4L)).thenReturn(List.of(audit));
        when(mapper.listSnapshots(4L)).thenReturn(List.of(snapshot));
        var result = service.get(4L);
        assertEquals(2L, result.auditTotal());
        assertEquals(1L, result.snapshotTotal());
        assertEquals("REJECT", result.audits().get(0).getDecision());
        assertEquals("RULES-demo", result.snapshots().get(0).getRuleVersion());
    }
}

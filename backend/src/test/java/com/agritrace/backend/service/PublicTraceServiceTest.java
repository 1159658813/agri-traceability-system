package com.agritrace.backend.service;

import com.agritrace.backend.dao.PublicTraceMapper;
import com.agritrace.backend.vo.trace.PublicTraceBatchRow;
import com.agritrace.backend.vo.trace.PublicTraceEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicTraceServiceTest {
    private final PublicTraceMapper mapper = mock(PublicTraceMapper.class);
    private final PublicTraceService service = new PublicTraceService(mapper);

    @Test
    void invalidCodeIsRejectedWithoutDatabaseQuery() {
        PublicTraceProblem error = assertThrows(PublicTraceProblem.class,
                () -> service.get("../../private"));
        assertEquals(400, error.getStatus());
        verifyNoInteractions(mapper);
    }

    @Test
    void unpublishedOrNonexistentCodeRevealsNothing() {
        PublicTraceProblem error = assertThrows(PublicTraceProblem.class,
                () -> service.get("UNPUBLISHED-001"));
        assertEquals(404, error.getStatus());
        verify(mapper).findPublished("UNPUBLISHED-001");
        verify(mapper, never()).recentEvents(anyLong());
    }

    @Test
    void publishedBatchReturnsOnlyApprovedFieldsAndChronologicalEvents() {
        LocalDateTime a = LocalDateTime.of(2026, 10, 1, 8, 0);
        LocalDateTime b = a.plusDays(2);
        when(mapper.findPublished("DEMO-TRACE-010")).thenReturn(batch());
        when(mapper.recentEvents(7L)).thenReturn(List.of(
                new PublicTraceEvent("CIRCULATION", b, "包装", null, null),
                new PublicTraceEvent("FARM", a, "灌溉", null, null)));
        var result = service.get("DEMO-TRACE-010");
        assertTrue(result.demo());
        assertEquals("FARM", result.events().get(0).stage());
        assertEquals("CIRCULATION", result.events().get(1).stage());
        assertFalse(result.moreEvents());
    }

    @Test
    void largeTimelineIsCappedAndMarkedAsPartial() {
        when(mapper.findPublished("DEMO-TRACE-010")).thenReturn(batch());
        var events = new ArrayList<PublicTraceEvent>();
        for (int i = 0; i < 201; i++) {
            events.add(new PublicTraceEvent("FARM", LocalDateTime.of(2026, 11, 1, 0, 0)
                    .minusMinutes(i), "灌溉", null, null));
        }
        when(mapper.recentEvents(7L)).thenReturn(events);
        var result = service.get("DEMO-TRACE-010");
        assertEquals(200, result.events().size());
        assertTrue(result.moreEvents());
    }

    private PublicTraceBatchRow batch() {
        return new PublicTraceBatchRow(7L, "DEMO-TYD-010", "DEMO-TRACE-010",
                "【演示】高丛蓝莓", "蓝莓", "水果", "【演示】种植基地", "山东省", "临沂市", "沂水县",
                LocalDate.of(2026, 9, 24), null, null, LocalDateTime.of(2026, 10, 30, 10, 0), true);
    }
}

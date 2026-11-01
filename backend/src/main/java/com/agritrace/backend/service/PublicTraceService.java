package com.agritrace.backend.service;

import com.agritrace.backend.dao.PublicTraceMapper;
import com.agritrace.backend.vo.trace.PublicTraceBatchRow;
import com.agritrace.backend.vo.trace.PublicTraceDetail;
import com.agritrace.backend.vo.trace.PublicTraceEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicTraceService {
    private static final Pattern TRACE_CODE = Pattern.compile("[A-Za-z0-9_-]{4,80}");
    private static final int MAX_EVENTS = 200;
    private final PublicTraceMapper mapper;

    public PublicTraceService(PublicTraceMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public PublicTraceDetail get(String traceCode) {
        if (traceCode == null || !TRACE_CODE.matcher(traceCode).matches()) {
            throw new PublicTraceProblem(400, "溯源码格式不正确");
        }
        PublicTraceBatchRow batch = mapper.findPublished(traceCode);
        if (batch == null) {
            // Never reveal whether an unpublished or private batch exists.
            throw new PublicTraceProblem(404, "未找到已发布的溯源批次");
        }
        List<PublicTraceEvent> latest = mapper.recentEvents(batch.id());
        if (latest == null) latest = List.of();
        boolean more = latest.size() > MAX_EVENTS;
        List<PublicTraceEvent> chronological = new ArrayList<>(
                latest.subList(0, Math.min(MAX_EVENTS, latest.size())));
        Collections.reverse(chronological);

        return new PublicTraceDetail(
                batch.batchNo(), batch.traceCode(), batch.productName(), batch.variety(),
                batch.categoryName(), batch.baseName(), batch.province(), batch.city(),
                batch.district(), batch.plantingDate(), batch.plannedHarvestDate(),
                batch.estimatedQuantityKg(), batch.publishedAt(), batch.demo(),
                List.copyOf(chronological), more);
    }
}

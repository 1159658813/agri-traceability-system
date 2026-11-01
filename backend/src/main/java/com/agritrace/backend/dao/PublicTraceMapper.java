package com.agritrace.backend.dao;

import com.agritrace.backend.vo.trace.PublicTraceBatchRow;
import com.agritrace.backend.vo.trace.PublicTraceEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PublicTraceMapper {
    @Select("""
            SELECT t.id, t.batch_no, t.trace_code, p.product_name, p.variety,
                   pc.category_name, b.base_name, b.province, b.city, b.district,
                   t.planting_date, t.planned_harvest_date, t.estimated_quantity_kg,
                   t.published_at,
                   CASE WHEN t.batch_no LIKE 'DEMO-%'
                              OR p.product_name LIKE '【演示】%'
                              OR b.base_name LIKE '【演示】%'
                              OR COALESCE(t.remark, '') LIKE '【演示】%'
                        THEN 1 ELSE 0 END AS demo
            FROM trace_batch t
            JOIN agri_product p ON p.id = t.product_id
                 AND p.owner_id = t.owner_id AND p.status = 1
            JOIN planting_base b ON b.id = t.base_id
                 AND b.owner_id = t.owner_id AND b.status = 1
            JOIN product_category pc ON pc.id = p.category_id AND pc.status = 1
            JOIN sys_user u ON u.id = t.owner_id
                 AND u.role_code = 'FARMER' AND u.status = 1
            WHERE t.trace_code = #{traceCode} AND t.status = 'PUBLISHED'
            """)
    @ConstructorArgs({
        @Arg(column = "id", javaType = Long.class),
        @Arg(column = "batch_no", javaType = String.class),
        @Arg(column = "trace_code", javaType = String.class),
        @Arg(column = "product_name", javaType = String.class),
        @Arg(column = "variety", javaType = String.class),
        @Arg(column = "category_name", javaType = String.class),
        @Arg(column = "base_name", javaType = String.class),
        @Arg(column = "province", javaType = String.class),
        @Arg(column = "city", javaType = String.class),
        @Arg(column = "district", javaType = String.class),
        @Arg(column = "planting_date", javaType = LocalDate.class),
        @Arg(column = "planned_harvest_date", javaType = LocalDate.class),
        @Arg(column = "estimated_quantity_kg", javaType = BigDecimal.class),
        @Arg(column = "published_at", javaType = LocalDateTime.class),
        @Arg(column = "demo", javaType = boolean.class)
    })
    PublicTraceBatchRow findPublished(@Param("traceCode") String traceCode);

    // A capped, newest-first feed. The service returns these in chronological order.
    // Free-text remarks, operator names, full base addresses and logistics numbers are excluded.
    @Select("""
            SELECT stage, occurred_at, title, detail, conclusion
            FROM (
              SELECT 'FARM' AS stage, f.operated_at AS occurred_at,
                     f.operation_type AS title,
                     CAST(NULL AS CHAR(500)) AS detail,
                     CAST(NULL AS CHAR(20)) AS conclusion, f.id AS event_id
              FROM farm_record f WHERE f.batch_id = #{batchId}
              UNION ALL
              SELECT 'HARVEST', h.harvested_at, '采收',
                     CONCAT('采收数量：', CAST(h.quantity_kg AS CHAR), ' kg',
                            CASE WHEN COALESCE(TRIM(h.grade), '') = '' THEN ''
                                 ELSE CONCAT('；等级：', h.grade) END),
                     NULL, h.id
              FROM harvest_record h WHERE h.batch_id = #{batchId}
              UNION ALL
              SELECT 'QUALITY', q.inspected_at, q.inspection_item,
                     NULL, q.conclusion, q.id
              FROM quality_inspection q WHERE q.batch_id = #{batchId}
              UNION ALL
              SELECT 'CIRCULATION', c.event_at, c.event_type,
                     NULL, NULL, c.id
              FROM circulation_record c WHERE c.batch_id = #{batchId}
            ) timeline
            ORDER BY occurred_at DESC, stage DESC, event_id DESC
            LIMIT 201
            """)
    @ConstructorArgs({
        @Arg(column = "stage", javaType = String.class),
        @Arg(column = "occurred_at", javaType = LocalDateTime.class),
        @Arg(column = "title", javaType = String.class),
        @Arg(column = "detail", javaType = String.class),
        @Arg(column = "conclusion", javaType = String.class)
    })
    List<PublicTraceEvent> recentEvents(@Param("batchId") Long batchId);
}

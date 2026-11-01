package com.agritrace.backend.dao;

import com.agritrace.backend.vo.farmer.CompletenessFacts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BatchCompletenessMapper {
    @Select("""
            SELECT t.status AS batch_status,
                   (SELECT COUNT(*) FROM planting_base b
                    WHERE b.id = t.base_id AND b.owner_id = t.owner_id) AS base_count,
                   (SELECT COUNT(*) FROM agri_product p
                    WHERE p.id = t.product_id AND p.owner_id = t.owner_id) AS product_count,
                   (SELECT COUNT(*) FROM farm_record f
                    WHERE f.batch_id = t.id) AS farm_count,
                   (SELECT COUNT(*) FROM harvest_record h
                    WHERE h.batch_id = t.id) AS harvest_count,
                   (SELECT COUNT(*) FROM quality_inspection q
                    WHERE q.batch_id = t.id AND q.inspected_at IS NOT NULL
                      AND q.inspection_item IS NOT NULL
                      AND TRIM(q.inspection_item) <> '') AS quality_count,
                   (SELECT COUNT(*) FROM circulation_record c
                    WHERE c.batch_id = t.id AND c.event_at IS NOT NULL
                      AND c.event_type IS NOT NULL
                      AND TRIM(c.event_type) <> '') AS circulation_count,
                   (SELECT COUNT(*) FROM quality_inspection q
                    WHERE q.batch_id = t.id AND q.conclusion = 'PENDING') AS pending_quality_count,
                   (SELECT COUNT(*) FROM quality_inspection q
                    WHERE q.batch_id = t.id AND q.conclusion = 'FAIL') AS failed_quality_count
            FROM trace_batch t
            WHERE t.id = #{batchId} AND t.owner_id = #{farmerId}
            """)
    @Results({
        @Result(column = "batch_status", property = "batchStatus"),
        @Result(column = "base_count", property = "baseCount"),
        @Result(column = "product_count", property = "productCount"),
        @Result(column = "farm_count", property = "farmCount"),
        @Result(column = "harvest_count", property = "harvestCount"),
        @Result(column = "quality_count", property = "qualityCount"),
        @Result(column = "circulation_count", property = "circulationCount"),
        @Result(column = "pending_quality_count", property = "pendingQualityCount"),
        @Result(column = "failed_quality_count", property = "failedQualityCount")
    })
    CompletenessFacts selectFacts(@Param("farmerId") Long farmerId,
                                  @Param("batchId") Long batchId);
}

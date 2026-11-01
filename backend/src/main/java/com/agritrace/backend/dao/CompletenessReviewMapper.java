package com.agritrace.backend.dao;

import com.agritrace.backend.vo.admin.CompletenessRuleConfig;
import com.agritrace.backend.vo.farmer.CompletenessFacts;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CompletenessReviewMapper {
    @Select("""
            SELECT rule_key, rule_name, weight, required_flag
            FROM completeness_rule WHERE enabled = 1 ORDER BY rule_key
            """)
    @Results({
        @Result(column = "rule_key", property = "ruleKey"),
        @Result(column = "rule_name", property = "ruleName"),
        @Result(column = "weight", property = "weight"),
        @Result(column = "required_flag", property = "required")
    })
    List<CompletenessRuleConfig> enabledRules();

    @Select("""
            SELECT t.status AS batch_status,
              (SELECT COUNT(*) FROM planting_base b
               WHERE b.id = t.base_id AND b.owner_id = t.owner_id AND b.status = 1
                 AND TRIM(b.base_name) <> '' AND TRIM(b.province) <> ''
                 AND TRIM(b.city) <> '' AND TRIM(b.address) <> '') AS base_count,
              (SELECT COUNT(*) FROM agri_product p
               WHERE p.id = t.product_id AND p.owner_id = t.owner_id AND p.status = 1
                 AND TRIM(p.product_name) <> '' AND COALESCE(TRIM(p.variety), '') <> '') AS product_count,
              (SELECT COUNT(*) FROM farm_record f WHERE f.batch_id = t.id
                 AND f.operated_at IS NOT NULL AND TRIM(f.operation_type) <> ''
                 AND TRIM(f.description) <> '') AS farm_count,
              (SELECT COUNT(*) FROM harvest_record h WHERE h.batch_id = t.id
                 AND h.harvested_at IS NOT NULL AND h.quantity_kg > 0) AS harvest_count,
              (SELECT COUNT(*) FROM quality_inspection q WHERE q.batch_id = t.id
                 AND q.inspected_at IS NOT NULL AND TRIM(q.inspection_item) <> ''
                 AND q.conclusion IN ('PENDING','PASS','FAIL')) AS quality_count,
              (SELECT COUNT(*) FROM circulation_record c WHERE c.batch_id = t.id
                 AND c.event_at IS NOT NULL AND TRIM(c.event_type) <> '') AS circulation_count,
              (SELECT COUNT(*) FROM quality_inspection q WHERE q.batch_id = t.id
                 AND q.conclusion = 'PENDING') AS pending_quality_count,
              (SELECT COUNT(*) FROM quality_inspection q WHERE q.batch_id = t.id
                 AND q.conclusion = 'FAIL') AS failed_quality_count,
              (SELECT COUNT(*) FROM quality_inspection q WHERE q.batch_id = t.id
                 AND q.conclusion = 'PASS' AND q.inspected_at IS NOT NULL
                 AND TRIM(q.inspection_item) <> '') AS passed_quality_count
            FROM trace_batch t WHERE t.id = #{batchId}
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
        @Result(column = "failed_quality_count", property = "failedQualityCount"),
        @Result(column = "passed_quality_count", property = "passedQualityCount")
    })
    CompletenessFacts factsForAdmin(@Param("batchId") Long batchId);

    @Insert("""
            INSERT INTO completeness_result (batch_id, rule_version, score, missing_items, evaluated_at)
            VALUES (#{batchId}, #{version}, #{score}, CAST(#{missingJson} AS JSON), NOW())
            """)
    int insertSnapshot(@Param("batchId") Long batchId,
                       @Param("version") String version,
                       @Param("score") BigDecimal score,
                       @Param("missingJson") String missingJson);
}

package com.agritrace.backend.dao;

import com.agritrace.backend.vo.admin.BatchReviewLock;
import com.agritrace.backend.vo.admin.BatchReviewRow;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminBatchReviewMapper {
    @Select("""
            SELECT COUNT(*) FROM trace_batch t
            WHERE t.status IN ('SUBMITTED','PUBLISHED','REJECTED')
              AND (#{status} = 'ALL' OR t.status = #{status})
            """)
    long count(@Param("status") String status);

    @Select("""
            SELECT t.id, t.batch_no, t.trace_code, p.product_name, b.base_name,
                   u.username, u.display_name, t.planting_date, t.planned_harvest_date,
                   t.status, t.remark, t.updated_at, t.published_at,
                   (SELECT COUNT(*) FROM farm_record fr WHERE fr.batch_id = t.id) AS farm_count,
                   (SELECT COUNT(*) FROM harvest_record hr WHERE hr.batch_id = t.id) AS harvest_count,
                   (SELECT COUNT(*) FROM quality_inspection qi WHERE qi.batch_id = t.id) AS quality_count,
                   (SELECT COUNT(*) FROM circulation_record cr WHERE cr.batch_id = t.id) AS circulation_count,
                   CASE WHEN p.product_name LIKE '【演示】%'
                            AND b.base_name LIKE '【演示】%'
                            AND COALESCE(t.remark, '') LIKE '【演示】%'
                        THEN 1 ELSE 0 END AS demo_eligible
            FROM trace_batch t
            JOIN agri_product p ON p.id = t.product_id
            JOIN planting_base b ON b.id = t.base_id
            JOIN sys_user u ON u.id = t.owner_id
            WHERE t.status IN ('SUBMITTED','PUBLISHED','REJECTED')
              AND (#{status} = 'ALL' OR t.status = #{status})
            ORDER BY t.updated_at DESC, t.id DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    @Results(id = "adminBatchReviewRow", value = {
        @Result(column = "id", property = "id", id = true),
        @Result(column = "batch_no", property = "batchNo"),
        @Result(column = "trace_code", property = "traceCode"),
        @Result(column = "product_name", property = "productName"),
        @Result(column = "base_name", property = "baseName"),
        @Result(column = "username", property = "username"),
        @Result(column = "display_name", property = "displayName"),
        @Result(column = "planting_date", property = "plantingDate"),
        @Result(column = "planned_harvest_date", property = "plannedHarvestDate"),
        @Result(column = "status", property = "status"),
        @Result(column = "remark", property = "remark"),
        @Result(column = "updated_at", property = "updatedAt"),
        @Result(column = "published_at", property = "publishedAt"),
        @Result(column = "farm_count", property = "farmCount"),
        @Result(column = "harvest_count", property = "harvestCount"),
        @Result(column = "quality_count", property = "qualityCount"),
        @Result(column = "circulation_count", property = "circulationCount"),
        @Result(column = "demo_eligible", property = "demoEligible")
    })
    List<BatchReviewRow> list(@Param("status") String status,
                              @Param("size") int size, @Param("offset") long offset);

    @Select("SELECT id, status FROM trace_batch WHERE id = #{id} FOR UPDATE")
    BatchReviewLock lockBatch(@Param("id") Long id);

    @Select("""
            SELECT COUNT(*) FROM trace_batch t
            JOIN agri_product p ON p.id = t.product_id AND p.owner_id = t.owner_id AND p.status = 1
            JOIN planting_base b ON b.id = t.base_id AND b.owner_id = t.owner_id AND b.status = 1
            JOIN sys_user u ON u.id = t.owner_id AND u.role_code = 'FARMER' AND u.status = 1
            WHERE t.id = #{id} AND t.status = 'SUBMITTED'
              AND t.planting_date IS NOT NULL
              AND p.product_name LIKE '【演示】%'
              AND b.base_name LIKE '【演示】%'
              AND COALESCE(t.remark, '') LIKE '【演示】%'
            """)
    int canPublishDemo(@Param("id") Long id);

    @Update("""
            UPDATE trace_batch SET status = 'PUBLISHED', published_at = NOW()
            WHERE id = #{id} AND status = 'SUBMITTED'
            """)
    int publish(@Param("id") Long id);

    @Update("""
            UPDATE trace_batch SET status = 'REJECTED', published_at = NULL
            WHERE id = #{id} AND status = 'SUBMITTED'
            """)
    int reject(@Param("id") Long id);

    @Insert("""
            INSERT INTO batch_audit (batch_id, reviewer_id, decision, comment, reviewed_at)
            VALUES (#{id}, #{reviewerId}, #{decision}, #{comment}, NOW())
            """)
    int writeAudit(@Param("id") Long id, @Param("reviewerId") Long reviewerId,
                   @Param("decision") String decision, @Param("comment") String comment);
}

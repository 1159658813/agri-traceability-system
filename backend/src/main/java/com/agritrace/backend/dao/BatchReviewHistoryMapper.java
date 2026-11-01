package com.agritrace.backend.dao;

import com.agritrace.backend.vo.admin.BatchAuditHistoryEntry;
import com.agritrace.backend.vo.admin.BatchScoreHistoryEntry;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BatchReviewHistoryMapper {
    @Select("SELECT COUNT(*) FROM trace_batch WHERE id = #{batchId}")
    long batchExists(@Param("batchId") Long batchId);

    @Select("SELECT COUNT(*) FROM batch_audit WHERE batch_id = #{batchId}")
    long auditCount(@Param("batchId") Long batchId);

    @Select("SELECT COUNT(*) FROM completeness_result WHERE batch_id = #{batchId}")
    long snapshotCount(@Param("batchId") Long batchId);

    @Select("""
            SELECT a.id, a.decision, a.comment, a.reviewed_at,
                   u.display_name AS reviewer_name
            FROM batch_audit a
            JOIN sys_user u ON u.id = a.reviewer_id
            WHERE a.batch_id = #{batchId}
            ORDER BY a.reviewed_at DESC, a.id DESC
            LIMIT 100
            """)
    @Results({
        @Result(column = "id", property = "id", id = true),
        @Result(column = "decision", property = "decision"),
        @Result(column = "comment", property = "comment"),
        @Result(column = "reviewer_name", property = "reviewerName"),
        @Result(column = "reviewed_at", property = "reviewedAt")
    })
    List<BatchAuditHistoryEntry> listAudits(@Param("batchId") Long batchId);

    @Select("""
            SELECT r.id, r.rule_version, r.score,
                   CAST(r.missing_items AS CHAR) AS missing_items_json,
                   r.evaluated_at
            FROM completeness_result r
            WHERE r.batch_id = #{batchId}
            ORDER BY r.evaluated_at DESC, r.id DESC
            LIMIT 100
            """)
    @Results({
        @Result(column = "id", property = "id", id = true),
        @Result(column = "rule_version", property = "ruleVersion"),
        @Result(column = "score", property = "score"),
        @Result(column = "missing_items_json", property = "missingItemsJson"),
        @Result(column = "evaluated_at", property = "evaluatedAt")
    })
    List<BatchScoreHistoryEntry> listSnapshots(@Param("batchId") Long batchId);
}

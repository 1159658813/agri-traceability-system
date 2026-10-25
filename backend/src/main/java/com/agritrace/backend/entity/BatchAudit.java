package com.agritrace.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 鎵规?瀹℃牳璁板綍
 * </p>
 *
 * @author agri-traceability
 * @since 2026-10-25
 */
@TableName("batch_audit")
public class BatchAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("batch_id")
    private Long batchId;

    @TableField("reviewer_id")
    private Long reviewerId;

    /**
     * APPROVE/REJECT
     */
    @TableField("decision")
    private String decision;

    @TableField("comment")
    private String comment;

    @TableField("reviewed_at")
    private LocalDateTime reviewedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    @Override
    public String toString() {
        return "BatchAudit{" +
            "id = " + id +
            ", batchId = " + batchId +
            ", reviewerId = " + reviewerId +
            ", decision = " + decision +
            ", comment = " + comment +
            ", reviewedAt = " + reviewedAt +
            "}";
    }
}

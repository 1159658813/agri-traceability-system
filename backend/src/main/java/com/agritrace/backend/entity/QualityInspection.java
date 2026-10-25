package com.agritrace.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 鎵规?璐ㄦ?璁板綍
 * </p>
 *
 * @author agri-traceability
 * @since 2026-10-25
 */
@TableName("quality_inspection")
public class QualityInspection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("batch_id")
    private Long batchId;

    @TableField("inspected_at")
    private LocalDateTime inspectedAt;

    @TableField("inspection_item")
    private String inspectionItem;

    @TableField("inspection_org")
    private String inspectionOrg;

    @TableField("report_no")
    private String reportNo;

    /**
     * PENDING/PASS/FAIL
     */
    @TableField("conclusion")
    private String conclusion;

    @TableField("remark")
    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;

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

    public LocalDateTime getInspectedAt() {
        return inspectedAt;
    }

    public void setInspectedAt(LocalDateTime inspectedAt) {
        this.inspectedAt = inspectedAt;
    }

    public String getInspectionItem() {
        return inspectionItem;
    }

    public void setInspectionItem(String inspectionItem) {
        this.inspectionItem = inspectionItem;
    }

    public String getInspectionOrg() {
        return inspectionOrg;
    }

    public void setInspectionOrg(String inspectionOrg) {
        this.inspectionOrg = inspectionOrg;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "QualityInspection{" +
            "id = " + id +
            ", batchId = " + batchId +
            ", inspectedAt = " + inspectedAt +
            ", inspectionItem = " + inspectionItem +
            ", inspectionOrg = " + inspectionOrg +
            ", reportNo = " + reportNo +
            ", conclusion = " + conclusion +
            ", remark = " + remark +
            ", createdAt = " + createdAt +
            "}";
    }
}

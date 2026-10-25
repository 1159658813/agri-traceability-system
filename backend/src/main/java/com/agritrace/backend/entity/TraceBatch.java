package com.agritrace.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 婧?簮鎵规?
 * </p>
 *
 * @author agri-traceability
 * @since 2026-10-25
 */
@TableName("trace_batch")
public class TraceBatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 鍐呴儴鎵规?缂栧彿
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 鍏?紑鏌ヨ?鐮侊紝鐢辨湇鍔＄?鐢熸垚锛屼笉鐩存帴浣跨敤涓婚敭
     */
    @TableField("trace_code")
    private String traceCode;

    @TableField("product_id")
    private Long productId;

    @TableField("base_id")
    private Long baseId;

    @TableField("owner_id")
    private Long ownerId;

    @TableField("planting_date")
    private LocalDate plantingDate;

    @TableField("planned_harvest_date")
    private LocalDate plannedHarvestDate;

    @TableField("estimated_quantity_kg")
    private BigDecimal estimatedQuantityKg;

    /**
     * DRAFT/SUBMITTED/PUBLISHED/REJECTED
     */
    @TableField("status")
    private String status;

    @TableField("remark")
    private String remark;

    @TableField("published_at")
    private LocalDateTime publishedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getTraceCode() {
        return traceCode;
    }

    public void setTraceCode(String traceCode) {
        this.traceCode = traceCode;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public LocalDate getPlannedHarvestDate() {
        return plannedHarvestDate;
    }

    public void setPlannedHarvestDate(LocalDate plannedHarvestDate) {
        this.plannedHarvestDate = plannedHarvestDate;
    }

    public BigDecimal getEstimatedQuantityKg() {
        return estimatedQuantityKg;
    }

    public void setEstimatedQuantityKg(BigDecimal estimatedQuantityKg) {
        this.estimatedQuantityKg = estimatedQuantityKg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TraceBatch{" +
            "id = " + id +
            ", batchNo = " + batchNo +
            ", traceCode = " + traceCode +
            ", productId = " + productId +
            ", baseId = " + baseId +
            ", ownerId = " + ownerId +
            ", plantingDate = " + plantingDate +
            ", plannedHarvestDate = " + plannedHarvestDate +
            ", estimatedQuantityKg = " + estimatedQuantityKg +
            ", status = " + status +
            ", remark = " + remark +
            ", publishedAt = " + publishedAt +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            "}";
    }
}

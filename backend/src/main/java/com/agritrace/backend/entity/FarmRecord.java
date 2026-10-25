package com.agritrace.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 绉嶆?涓庡啘浜嬭?褰
 * </p>
 *
 * @author agri-traceability
 * @since 2026-10-25
 */
@TableName("farm_record")
public class FarmRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("batch_id")
    private Long batchId;

    /**
     * 鏂借偉/鐏屾簤/鐥呰櫕瀹抽槻娌荤瓑锛岀敱涓氬姟瀛楀吀绾︽潫
     */
    @TableField("operation_type")
    private String operationType;

    @TableField("operated_at")
    private LocalDateTime operatedAt;

    @TableField("description")
    private String description;

    @TableField("operator_name")
    private String operatorName;

    @TableField("created_by")
    private Long createdBy;

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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getOperatedAt() {
        return operatedAt;
    }

    public void setOperatedAt(LocalDateTime operatedAt) {
        this.operatedAt = operatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "FarmRecord{" +
            "id = " + id +
            ", batchId = " + batchId +
            ", operationType = " + operationType +
            ", operatedAt = " + operatedAt +
            ", description = " + description +
            ", operatorName = " + operatorName +
            ", createdBy = " + createdBy +
            ", createdAt = " + createdAt +
            "}";
    }
}

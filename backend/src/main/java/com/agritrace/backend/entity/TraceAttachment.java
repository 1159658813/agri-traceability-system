package com.agritrace.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 婧?簮璧勬枡闄勪欢
 * </p>
 *
 * @author agri-traceability
 * @since 2026-10-25
 */
@TableName("trace_attachment")
public class TraceAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("batch_id")
    private Long batchId;

    /**
     * BATCH/FARM/HARVEST/QUALITY/CIRCULATION
     */
    @TableField("related_type")
    private String relatedType;

    /**
     * 鍏宠仈瀵瑰簲璁板綍ID锛屾寜绫诲瀷鍦ㄤ笟鍔″眰鏍搁獙
     */
    @TableField("related_id")
    private Long relatedId;

    @TableField("file_name")
    private String fileName;

    @TableField("file_url")
    private String fileUrl;

    @TableField("file_type")
    private String fileType;

    @TableField("public_visible")
    private Byte publicVisible;

    @TableField("uploaded_by")
    private Long uploadedBy;

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

    public String getRelatedType() {
        return relatedType;
    }

    public void setRelatedType(String relatedType) {
        this.relatedType = relatedType;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Byte getPublicVisible() {
        return publicVisible;
    }

    public void setPublicVisible(Byte publicVisible) {
        this.publicVisible = publicVisible;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TraceAttachment{" +
            "id = " + id +
            ", batchId = " + batchId +
            ", relatedType = " + relatedType +
            ", relatedId = " + relatedId +
            ", fileName = " + fileName +
            ", fileUrl = " + fileUrl +
            ", fileType = " + fileType +
            ", publicVisible = " + publicVisible +
            ", uploadedBy = " + uploadedBy +
            ", createdAt = " + createdAt +
            "}";
    }
}

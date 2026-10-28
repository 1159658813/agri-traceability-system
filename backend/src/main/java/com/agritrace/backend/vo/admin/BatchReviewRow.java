package com.agritrace.backend.vo.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BatchReviewRow {
    private Long id;
    private String batchNo;
    private String traceCode;
    private String productName;
    private String baseName;
    private String username;
    private String displayName;
    private LocalDate plantingDate;
    private LocalDate plannedHarvestDate;
    private String status;
    private String remark;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private int farmCount;
    private int harvestCount;
    private int qualityCount;
    private int circulationCount;
    private boolean demoEligible;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getTraceCode() { return traceCode; }
    public void setTraceCode(String traceCode) { this.traceCode = traceCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBaseName() { return baseName; }
    public void setBaseName(String baseName) { this.baseName = baseName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public LocalDate getPlantingDate() { return plantingDate; }
    public void setPlantingDate(LocalDate plantingDate) { this.plantingDate = plantingDate; }
    public LocalDate getPlannedHarvestDate() { return plannedHarvestDate; }
    public void setPlannedHarvestDate(LocalDate plannedHarvestDate) { this.plannedHarvestDate = plannedHarvestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public int getFarmCount() { return farmCount; }
    public void setFarmCount(int farmCount) { this.farmCount = farmCount; }
    public int getHarvestCount() { return harvestCount; }
    public void setHarvestCount(int harvestCount) { this.harvestCount = harvestCount; }
    public int getQualityCount() { return qualityCount; }
    public void setQualityCount(int qualityCount) { this.qualityCount = qualityCount; }
    public int getCirculationCount() { return circulationCount; }
    public void setCirculationCount(int circulationCount) { this.circulationCount = circulationCount; }
    public boolean isDemoEligible() { return demoEligible; }
    public void setDemoEligible(boolean demoEligible) { this.demoEligible = demoEligible; }
}

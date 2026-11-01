package com.agritrace.backend.vo.farmer;

public class CompletenessFacts {
    private String batchStatus;
    private int baseCount;
    private int productCount;
    private int farmCount;
    private int harvestCount;
    private int qualityCount;
    private int circulationCount;
    private int pendingQualityCount;
    private int failedQualityCount;

    public String getBatchStatus() { return batchStatus; }
    public void setBatchStatus(String batchStatus) { this.batchStatus = batchStatus; }
    public int getBaseCount() { return baseCount; }
    public void setBaseCount(int baseCount) { this.baseCount = baseCount; }
    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }
    public int getFarmCount() { return farmCount; }
    public void setFarmCount(int farmCount) { this.farmCount = farmCount; }
    public int getHarvestCount() { return harvestCount; }
    public void setHarvestCount(int harvestCount) { this.harvestCount = harvestCount; }
    public int getQualityCount() { return qualityCount; }
    public void setQualityCount(int qualityCount) { this.qualityCount = qualityCount; }
    public int getCirculationCount() { return circulationCount; }
    public void setCirculationCount(int circulationCount) { this.circulationCount = circulationCount; }
    public int getPendingQualityCount() { return pendingQualityCount; }
    public void setPendingQualityCount(int pendingQualityCount) { this.pendingQualityCount = pendingQualityCount; }
    public int getFailedQualityCount() { return failedQualityCount; }
    public void setFailedQualityCount(int failedQualityCount) { this.failedQualityCount = failedQualityCount; }
}

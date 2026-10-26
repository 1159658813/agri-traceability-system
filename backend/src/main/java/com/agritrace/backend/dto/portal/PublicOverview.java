package com.agritrace.backend.dto.portal;

public class PublicOverview {
    private long publishedBatches;
    private long productCount;
    private long baseCount;
    private long regionCount;

    public long getPublishedBatches() { return publishedBatches; }
    public void setPublishedBatches(long publishedBatches) { this.publishedBatches = publishedBatches; }
    public long getProductCount() { return productCount; }
    public void setProductCount(long productCount) { this.productCount = productCount; }
    public long getBaseCount() { return baseCount; }
    public void setBaseCount(long baseCount) { this.baseCount = baseCount; }
    public long getRegionCount() { return regionCount; }
    public void setRegionCount(long regionCount) { this.regionCount = regionCount; }
}

package com.agritrace.backend.vo.farmer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HarvestRow {
    private Long id;
    private LocalDateTime harvestedAt;
    private BigDecimal quantityKg;
    private String grade;
    private String remark;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getHarvestedAt() { return harvestedAt; }
    public void setHarvestedAt(LocalDateTime harvestedAt) { this.harvestedAt = harvestedAt; }
    public BigDecimal getQuantityKg() { return quantityKg; }
    public void setQuantityKg(BigDecimal quantityKg) { this.quantityKg = quantityKg; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

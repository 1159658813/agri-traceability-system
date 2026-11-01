package com.agritrace.backend.vo.farmer;

import java.time.LocalDateTime;

public class QualityInspectionRow {
    private Long id;
    private LocalDateTime inspectedAt;
    private String inspectionItem;
    private String inspectionOrg;
    private String reportNo;
    private String conclusion;
    private String remark;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getInspectedAt() { return inspectedAt; }
    public void setInspectedAt(LocalDateTime inspectedAt) { this.inspectedAt = inspectedAt; }
    public String getInspectionItem() { return inspectionItem; }
    public void setInspectionItem(String inspectionItem) { this.inspectionItem = inspectionItem; }
    public String getInspectionOrg() { return inspectionOrg; }
    public void setInspectionOrg(String inspectionOrg) { this.inspectionOrg = inspectionOrg; }
    public String getReportNo() { return reportNo; }
    public void setReportNo(String reportNo) { this.reportNo = reportNo; }
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

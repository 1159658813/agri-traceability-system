package com.agritrace.backend.vo.farmer;

import java.time.LocalDateTime;

public class FarmerApplicationRow {
    private Long id;
    private String username;
    private String applicantType;
    private String organizationName;
    private String contactName;
    private String contactPhone;
    private String farmingLocation;
    private String proofType;
    private String applicationNote;
    private String reviewStatus;
    private String reviewComment;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private Boolean demoProof;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getApplicantType() { return applicantType; }
    public void setApplicantType(String applicantType) { this.applicantType = applicantType; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getFarmingLocation() { return farmingLocation; }
    public void setFarmingLocation(String farmingLocation) { this.farmingLocation = farmingLocation; }
    public String getProofType() { return proofType; }
    public void setProofType(String proofType) { this.proofType = proofType; }
    public String getApplicationNote() { return applicationNote; }
    public void setApplicationNote(String applicationNote) { this.applicationNote = applicationNote; }
    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public Boolean getDemoProof() { return demoProof; }
    public void setDemoProof(Boolean demoProof) { this.demoProof = demoProof; }
}

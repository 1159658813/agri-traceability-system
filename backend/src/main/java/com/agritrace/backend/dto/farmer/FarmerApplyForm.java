package com.agritrace.backend.dto.farmer;

import org.springframework.web.multipart.MultipartFile;

public class FarmerApplyForm {
    private String username;
    private String password;
    private String applicantType;
    private String organizationName;
    private String contactName;
    private String contactPhone;
    private String farmingLocation;
    private String proofType;
    private String applicationNote;
    private MultipartFile proofFile;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
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
    public MultipartFile getProofFile() { return proofFile; }
    public void setProofFile(MultipartFile proofFile) { this.proofFile = proofFile; }
}

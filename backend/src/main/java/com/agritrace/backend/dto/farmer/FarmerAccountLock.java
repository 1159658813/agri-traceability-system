package com.agritrace.backend.dto.farmer;

public class FarmerAccountLock {
    private String roleCode;
    private Integer status;

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

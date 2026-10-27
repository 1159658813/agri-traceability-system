package com.agritrace.backend.dto.farmer;

public class NewFarmerAccount {
    private Long id;
    private String username;
    private String passwordHash;
    private String displayName;

    public NewFarmerAccount(String username, String passwordHash, String displayName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
}

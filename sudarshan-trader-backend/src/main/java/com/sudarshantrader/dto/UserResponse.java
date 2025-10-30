package com.sudarshantrader.dto;

public class UserResponse {
    private Long id;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String gst;
    private String role;
    private boolean verified;

    public UserResponse(Long id, String companyName, String contactPerson, String email, String phone, String gst,
            String role, boolean verified) {
        this.id = id;
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.gst = gst;
        this.role = role;
        this.verified = verified;
    }

    // Getters only
    public Long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getGst() {
        return gst;
    }

    public String getRole() {
        return role;
    }

    public boolean isVerified() {
        return verified;
    }
}

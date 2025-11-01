package com.sudarshan.trader.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String gstNumber;
    private String role;
    private Boolean verified;
}

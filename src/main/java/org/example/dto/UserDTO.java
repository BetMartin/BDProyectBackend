package org.example.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String userName;
    private String password;
    private int dni;
    private String firstName;
    private String lastName;
    private String phone;
    private String apartment;
    private String street;
    private Integer streetNumber;
    private Long provinceId;
    private String provinceName;
    private String address;
    private RolDTO rol;
}
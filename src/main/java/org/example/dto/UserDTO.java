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
    private int phone;
    private String address;
    private RolDTO rol;
}
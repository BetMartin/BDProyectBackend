package org.example.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String userName;
    private String password;
    private RolDTO rol;

}
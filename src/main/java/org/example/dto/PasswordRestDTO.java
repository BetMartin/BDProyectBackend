package org.example.dto;

import lombok.Data;

@Data
public class PasswordRestDTO {
    private String token;
    private String newPassword;
}

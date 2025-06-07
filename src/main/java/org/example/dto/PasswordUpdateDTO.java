package org.example.dto;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private String token;
    private String newPassword;
}
package com.example.idatt2106_2022_05_backend.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginDto {

    @Email
    private String email;

    private String password;
}

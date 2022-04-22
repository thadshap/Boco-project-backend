package com.example.idatt2106_2022_05_backend.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateDto {

    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String password;
    private byte[] picture;
}

package com.example.idatt2106_2022_05_backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserReturnDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String role;

    private boolean verified;

    private double rating;
}

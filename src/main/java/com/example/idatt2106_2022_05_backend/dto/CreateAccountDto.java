package com.example.idatt2106_2022_05_backend.dto;

import lombok.Data;

@Data
public class CreateAccountDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String matchingPassword;

}

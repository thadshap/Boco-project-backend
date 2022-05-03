package com.example.idatt2106_2022_05_backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReturnDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

    private boolean verified;

    private double rating;

    private String pictureUrl;
}

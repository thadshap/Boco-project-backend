package com.example.idatt2106_2022_05_backend.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "data transfer object for logging and authenticating in a user")
public class LoginDto {

    @Email
    @ApiModelProperty(notes = "users email")
    private String email;

    @ApiModelProperty(notes = "users types in password")
    private String password;
}

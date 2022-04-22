package com.example.idatt2106_2022_05_backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "data transfer object for creating a user")
public class CreateAccountDto {

    @ApiModelProperty(notes = "firstname of user")
    private String firstName;

    @ApiModelProperty(notes = "lastname of user")
    private String lastName;

    @ApiModelProperty(notes = "users email")
    private String email;

    @ApiModelProperty(notes = "password")
    private String password;

    @ApiModelProperty(notes = "matching password to control that user typed in correctly")
    private String matchingPassword;

}

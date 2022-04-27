package com.example.idatt2106_2022_05_backend.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@ApiModel(description = "data transfer object to update user info")
public class UserUpdateDto {

    @ApiModelProperty(notes = "first name of user")
    private String firstName;

    @ApiModelProperty(notes = "last name of user")
    private String lastName;

    @Email
    @ApiModelProperty(notes = "users email")
    private String email;

    @ApiModelProperty(notes = "users password")
    private String password;

    @ApiModelProperty(notes = "profile picture")
    private MultipartFile picture;
}

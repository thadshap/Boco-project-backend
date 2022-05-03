package com.example.idatt2106_2022_05_backend.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "data transfer object to update user info")
public class UserUpdateDto {

    @ApiModelProperty(notes = "first name of user")
    private String firstName;

    @ApiModelProperty(notes = "last name of user")
    private String lastName;

    @ApiModelProperty(notes = "users password")
    private String password;
}

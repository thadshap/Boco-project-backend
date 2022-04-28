package com.example.idatt2106_2022_05_backend.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "data transfer object to update forgotten password")
public class UserRenewPasswordDto {

    @ApiModelProperty(notes = "new password")
    private String password;

    @ApiModelProperty(notes = "confirmation of new password")
    private String confirmPassword;
}

package com.example.idatt2106_2022_05_backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "token returned to user upon authentication")
public class LoginResponse {

//    @ApiOperation(notes = "user id")
    private Long id;

    @ApiModelProperty(notes = "token String")
    private String token;
}

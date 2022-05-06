package com.example.idatt2106_2022_05_backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Data transfer object for picture, used to send and return pictures")
public class PictureReturnDto {

    @ApiModelProperty(notes = "id of picture")
    public Long id;

    @ApiModelProperty(notes = "type of image format")
    private String type;

    @ApiModelProperty(notes = "base64 string of picture")
    private String base64;
}

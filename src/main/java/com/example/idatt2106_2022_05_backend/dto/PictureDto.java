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
@ApiModel(description = "Data transfer object for pictures belonging to ad or user (profile picture)")
public class PictureDto {
    @ApiModelProperty(notes = "id of the picture")
    private Long id;

    @ApiModelProperty(notes = "the filetype")
    private String type;

    @ApiModelProperty(notes = "the picture in the shape of a byte array")
    private byte[] data;

    @ApiModelProperty(notes = "id of the ad this picture is owned by")
    private long adId;

    @ApiModelProperty(notes = "id of the user that posted the ad")
    private long userId;
}

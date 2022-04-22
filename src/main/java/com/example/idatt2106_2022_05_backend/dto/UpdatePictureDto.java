package com.example.idatt2106_2022_05_backend.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@ApiModel(description = "data transfer object to delete or upload a picture")
public class UpdatePictureDto {

    @ApiModelProperty(notes = "id of the ad the picture belongs to")
    private long ad_id;

    //TODO: fix this
    @ApiModelProperty(notes = "id of the picture the ")
    private String filename;

    @ApiModelProperty(notes = "multipartfile containing the new picture")
    private MultipartFile file;
}

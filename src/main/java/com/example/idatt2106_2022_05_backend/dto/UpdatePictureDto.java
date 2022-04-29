package com.example.idatt2106_2022_05_backend.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@ApiModel(description = "data transfer object to delete or upload a picture")
public class UpdatePictureDto {

    @ApiModelProperty(notes = "id of the ad the picture belongs to")
    private long id;

    @ApiModelProperty(notes = "multipartFile containing the new picture")
    private MultipartFile multipartFile;
}

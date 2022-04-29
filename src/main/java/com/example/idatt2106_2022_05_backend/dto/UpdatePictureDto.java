package com.example.idatt2106_2022_05_backend.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ApiModel(description = "data transfer object to delete or upload a picture")
public class UpdatePictureDto {

    @ApiModelProperty(notes = "id of the ad the picture belongs to")
    private long id;

    @ApiModelProperty(notes = "multipartFile containing the new picture")
    private MultipartFile multipartFile;
}

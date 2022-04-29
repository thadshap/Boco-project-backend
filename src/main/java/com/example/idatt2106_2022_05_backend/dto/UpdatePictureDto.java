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

    @ApiModelProperty(notes = "id of the ad the picture belongs to if the user wished to change its ad picture")
    private long id; // todo security breach because a user can delete an ad's photo without there being a check for
    // todo the user doing it having the correct user id. We can use the user id below for this!

    @ApiModelProperty(notes = "id of the user the picture belongs to in case of changing profile picture")
    private long userId;

    @ApiModelProperty(notes = "multipartFile containing the new picture")
    private MultipartFile multipartFile;
}

package com.example.idatt2106_2022_05_backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "data transfer object to create and return reviews on an ad")
public class ReviewDto {

    @ApiModelProperty(notes = "rating of ad")
    private int rating;

    @ApiModelProperty(notes = "description in review")
    private String description;

    @ApiModelProperty(notes = "id of user writing the review")
    private long userId;

    @ApiModelProperty(notes = "id of the ad the user writes a review on")
    private long adId;
}

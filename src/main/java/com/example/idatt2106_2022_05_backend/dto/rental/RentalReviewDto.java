package com.example.idatt2106_2022_05_backend.dto.rental;

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
@ApiModel(description = "Data transfer object for a rental review, used for creating and deleting reviews")
public class RentalReviewDto {

    @ApiModelProperty(notes = "rating of ad/rental, 1-10 stars")
    private double rating;

    @ApiModelProperty(notes = "review text")
    private String review;
}

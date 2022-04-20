package com.example.idatt2106_2022_05_backend.dto.Review;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description="Sending back reviews for requested ad or user")
public class ReviewDto {
    @ApiModelProperty(notes="rating between 1 and 10")
    private int rating;

    @ApiModelProperty(notes="description of rental, ad or communication")
    private String description;
}

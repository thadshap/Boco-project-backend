package com.example.idatt2106_2022_05_backend.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "Data transfer object to return a user")
public class UserReturnDto {

    private Long id;

    @ApiModelProperty(notes = "first name of user")
    private String firstName;

    @ApiModelProperty(notes = "last name of user")
    private String lastName;

    @ApiModelProperty(notes = "email of user")
    private String email;

    @ApiModelProperty(notes = "role of user: user, admin<")
    private String role;

    @ApiModelProperty(notes = "true if user is verified")
    private boolean verified;

    @ApiModelProperty(notes = "total rating of user")
    private double rating;

    @ApiModelProperty(notes = "picture url for user profile picture")
    private String pictureUrl;

    @ApiModelProperty(notes = "number of reviews user has gotten")
    private long nrOfReviews;
}

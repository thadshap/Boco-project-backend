package com.example.idatt2106_2022_05_backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "data transfer object for requesting ads, user sends with the location to calculate distance")
public class UserGeoLocation {

    @ApiModelProperty(notes = "latitude")
    private double lat;

    @ApiModelProperty(notes = "longitude")
    private double lng;

    @ApiModelProperty(notes = "amount of ads requested")
    private int amount;
}

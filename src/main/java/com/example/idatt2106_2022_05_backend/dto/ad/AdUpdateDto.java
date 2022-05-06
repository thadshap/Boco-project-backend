package com.example.idatt2106_2022_05_backend.dto.ad;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Data transfer object for Ad, used for updating Ad information")
public class AdUpdateDto {
    @ApiModelProperty(notes = "title of ad")
    private String title;

    @ApiModelProperty(notes = "description of item/ad")
    private String description;

    @ApiModelProperty(notes = "duration of rental")
    private int duration;

    @ApiModelProperty(notes = "unity of rental: hour, day, week or month")
    private AdType durationType;

    @ApiModelProperty(notes = "price of renting item")
    private int price;

    @ApiModelProperty(notes = "location streetAddress of item")
    private String streetAddress;

    @ApiModelProperty(notes = "postalCode of items location")
    private int postalCode;

    @ApiModelProperty(notes = "boolean true if item is currently rented out")
    private boolean rentedOut;

    @ApiModelProperty(notes = "city postalCode belongs to ")
    private String city;

    public boolean getRentedOut() {
        return rentedOut;
    }
}

package com.example.idatt2106_2022_05_backend.dto;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Data transfer object upon receipt from frontend MUST contain: - rental (being rented out or given away) - rentedOut
 * (true if the item is rented out) - duration (quantity of duration type) - durationType (type of duration --> see
 * "AdType" enum) - categoryId (only the id of the nearest category) - price - street_address (of the item) -
 * postal_code (of the item) - title (header of the ad)
 *
 * Data transfer object upon receipt from fronted CAN contain: - description - picture (pictures of the item to be
 * rented out)
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ApiModel(description = "Data transfer object for Ad, used for creation of Ad and returning available ads")
public class AdDto {
    @ApiModelProperty(notes = "boolean true if item is for rent, false if it is for sale")
    private boolean rental;

    @ApiModelProperty(notes = "boolean true if item is currently rented out")
    private boolean rentedOut;

    @ApiModelProperty(notes = "Duration of rental")
    private int duration;

    @ApiModelProperty(notes = "unity of rental: hour, day, week or month")
    private AdType durationType;

    @ApiModelProperty(notes = "Id of category the ad belongs to")
    private long categoryId;

    @ApiModelProperty(notes = "price of renting item")
    private int price;

    @ApiModelProperty(notes = "location streetaddress of item")
    private String streetAddress;

    @ApiModelProperty(notes = "postalcode of items location")
    private int postalCode;

    @ApiModelProperty(notes = "title of ad")
    private String title; // title/header

    @ApiModelProperty(notes = "description of item/ad")
    private String description;

    @ApiModelProperty(notes = "distance between user and the location of ad in km")
    private double distance;

    // Upon POST-request todo ex: https://www.techgeeknext.com/spring-boot/spring-boot-upload-image
    private Set<MultipartFile> picturesIn;

    // Upon GET-request todo ex: https://www.techgeeknext.com/spring-boot/spring-boot-upload-image
    private Set<Image> picturesOut;

    // Upon update-methods
    @ApiModelProperty(notes = "Id of ad")
    private long adId;

    @ApiModelProperty(notes = "id of user creating the ad")
    private long user_id;
}

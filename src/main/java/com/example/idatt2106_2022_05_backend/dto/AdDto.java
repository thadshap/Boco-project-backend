package com.example.idatt2106_2022_05_backend.dto;

import com.example.idatt2106_2022_05_backend.enums.AdType;
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
public class AdDto {
    private boolean rental;
    private boolean rentedOut;
    private int duration;
    private AdType durationType;
    private long categoryId;
    private int price;
    private String streetAddress;
    private int postalCode;
    private String title; // title/header
    private String description;
    private double distance;

    // Upon POST-request todo ex: https://www.techgeeknext.com/spring-boot/spring-boot-upload-image
    private ArrayList<MultipartFile> picturesIn;

    // Upon GET-request todo ex: https://www.techgeeknext.com/spring-boot/spring-boot-upload-image
    private ArrayList<Image> picturesOut;

    // Upon update-methods
    private long adId;

}

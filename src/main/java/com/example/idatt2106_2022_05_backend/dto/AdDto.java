package com.example.idatt2106_2022_05_backend.dto;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.Set;

/**
 * Data transfer object upon receipt from frontend MUST contain:
 *          - rental (being rented out or given away)
 *          - rentedOut (true if the item is rented out)
 *          - duration (quantity of duration type)
 *          - durationType (type of duration --> see "AdType" enum)
 *          - categoryId (only the id of the nearest category)
 *          - price
 *          - street_address (of the item)
 *          - postal_code (of the item)
 *          - name (header of the ad)
 *
 * Data transfer object upon receipt from fronted CAN contain:
 *              - description
 *              - picture (pictures of the item to be rented out)
 */
public class AdDto {
    private boolean rental;
    private boolean rentedOut;
    private int duration;
    private AdType durationType;
    private long categoryId;
    private int price;
    private String streetAddress;
    private int postalCode;
    private String name; // title/header
    private String description;

    // Upon POST-request todo ex: https://www.techgeeknext.com/spring-boot/spring-boot-upload-image
    private MultipartFile picturesIn;

    // Upon GET-request todo ex: https://www.techgeeknext.com/spring-boot/spring-boot-upload-image
    private Set<Image> picturesOut;

    // GETTERS

    public boolean isRental() {
        return rental;
    }

    public boolean isRentedOut() {
        return rentedOut;
    }

    public int getDuration() {
        return duration;
    }

    public AdType getDurationType() {
        return durationType;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public int getPrice() {
        return price;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public MultipartFile getPicturesIn() {
        return picturesIn;
    }

    public Set<Image> getPicturesOut() {
        return picturesOut;
    }

    // SETTERS

    public void setRental(boolean rental) {
        this.rental = rental;
    }

    public void setRentedOut(boolean rentedOut) {
        this.rentedOut = rentedOut;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDurationType(AdType durationType) {
        this.durationType = durationType;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicturesIn(MultipartFile picturesIn) {
        this.picturesIn = picturesIn;
    }

    public void setPicturesOut(Set<Image> picturesOut) {
        this.picturesOut = picturesOut;
    }
}

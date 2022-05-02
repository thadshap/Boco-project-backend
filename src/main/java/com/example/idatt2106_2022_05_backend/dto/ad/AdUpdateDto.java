package com.example.idatt2106_2022_05_backend.dto.ad;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AdUpdateDto {

    private String title;
    private String description;
    private int duration;
    private AdType durationType;
    private int price;
    private String streetAddress;
    private int postalCode;
    private boolean rentedOut;

    @ApiModelProperty(notes = "city postalCode belongs to ")
    private String city;

    public boolean getRentedOut() {
        return rentedOut;
    }
}

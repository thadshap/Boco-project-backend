package com.example.idatt2106_2022_05_backend.dto.ad;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class FilterListOfAds {
    private List<AdDto> list;
    private String filterType;
    private double upperLimit;
    private double lowerLimit;
    // When a user has clicked on a category and wants to only get ads within that category
    private String category;

    private boolean lowestValueFirst;
    private double lat;
    private double lng;
}

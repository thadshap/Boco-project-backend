package com.example.idatt2106_2022_05_backend.dto;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import lombok.Data;

import java.util.List;

@Data
public class FilterListOfAds {
    private List<AdDto> list;
    private double upperLimit;
    private double lowerLimit;
}
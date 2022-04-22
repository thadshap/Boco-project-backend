package com.example.idatt2106_2022_05_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGeoLocation {
    double lat;
    double lng;
}

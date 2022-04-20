package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

@Service
public interface GeoLocationService {

    Response getAllGeoLocations();

    Response calculateDistance(double lat1, double lng1, double lat2, double lng2);

    Response getById();

    Response deleteById();

    Response addNewGeoLocation(GeoLo);


}

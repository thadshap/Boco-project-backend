package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.model.GeoLocation;
import com.example.idatt2106_2022_05_backend.service.Ad.GeoLocationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoLocationServiceTest {


    @Test
    void calculateDistanceInMeters(){
        GeoLocationService service = new GeoLocationService();
        UserGeoLocation userGeoLocation = new UserGeoLocation();
        userGeoLocation.setLat(63.424595);
        userGeoLocation.setLng(10.810314);

        GeoLocation itemGeoLocation = new GeoLocation();
        itemGeoLocation.setGeo_id(1);
        itemGeoLocation.setLat(63.442858);
        itemGeoLocation.setLng(10.868397);

        double dist = service.calculateDistanceInMeters(userGeoLocation.getLat(),userGeoLocation.getLng(), itemGeoLocation.getLat(), itemGeoLocation.getLng());
        System.out.println("Distance between the given points should be about 3,5km" + dist);
        assertTrue(3.4<dist && dist<36 );
    }
}
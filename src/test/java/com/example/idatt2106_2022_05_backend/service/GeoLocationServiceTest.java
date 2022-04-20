package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.model.GeoLocation;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class GeoLocationServiceTest {

    @Autowired
    GeoLocationService service;

    @BeforeEach
    void setUp() {
        /*
        Distance between these two locations should be about 3km
         */


    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void calculateDistance1() {
        UserGeoLocation userGeoLocation = new UserGeoLocation();
        userGeoLocation.setLat(63.424595);
        userGeoLocation.setLng(10.810314);

        GeoLocation itemGeoLocation = new GeoLocation();
        itemGeoLocation.setGeo_id(1);
        itemGeoLocation.setLat(63.442858);
        itemGeoLocation.setLng(10.868397);

        double dist = service.calculateDistance1(userGeoLocation, itemGeoLocation);
        System.out.println(dist);
        assertTrue(3<dist );
    }

    @Test
    void calculateDistance2() {
        UserGeoLocation userGeoLocation = new UserGeoLocation();
        userGeoLocation.setLat(63.424595);
        userGeoLocation.setLng(10.810314);

        GeoLocation itemGeoLocation = new GeoLocation();
        itemGeoLocation.setGeo_id(1);
        itemGeoLocation.setLat(63.442858);
        itemGeoLocation.setLng(10.868397);

        double dist = service.calculateDistance2(userGeoLocation, itemGeoLocation);
        System.out.println(dist);
        assertTrue(3<dist );
    }
}
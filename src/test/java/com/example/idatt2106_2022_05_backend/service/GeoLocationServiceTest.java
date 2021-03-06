package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.dto.user.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.service.ad.AdServiceImpl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeoLocationServiceTest {

    @Test
    void calculateDistanceInMeters() {
        AdServiceImpl service = new AdServiceImpl();
        UserGeoLocation userGeoLocation = new UserGeoLocation();
        userGeoLocation.setLat(63.424595);
        userGeoLocation.setLng(10.810314);

        UserGeoLocation itemGeoLocation = new UserGeoLocation();
        itemGeoLocation.setLat(63.442858);
        itemGeoLocation.setLng(10.868397);

        double dist = service.calculateDistance(userGeoLocation.getLat(), userGeoLocation.getLng(),
                itemGeoLocation.getLat(), itemGeoLocation.getLng());
        assertTrue(3.4 < dist && dist < 3.6);
    }
}

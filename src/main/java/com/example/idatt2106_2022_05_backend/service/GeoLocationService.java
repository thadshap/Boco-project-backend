package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.model.GeoLocation;
import org.springframework.stereotype.Service;

@Service
public class GeoLocationService {

    private double EART_RADIUS = 6371.01;

    /*
    Need:
    1. Recieve location from user
    2. Method to calculate distance between two geolocation
    3. Passing all ads through calculation in AdService
     */

    /*
    We should recieve the geolocation while the user requests all ads
     */


    public double calculateDistance1(UserGeoLocation userGeoLocation, GeoLocation itemGeoLocation){
        double temp = Math.cos(Math.toRadians(userGeoLocation.getLat()))
                *Math.cos(Math.toRadians(itemGeoLocation.getLat()))
                *Math.cos(Math.toRadians(itemGeoLocation.getLat()-userGeoLocation.getLat()))
                +Math.sin(Math.toRadians(userGeoLocation.getLat()))
                *Math.sin(Math.toRadians(itemGeoLocation.getLat()));
        return temp * EART_RADIUS * Math.PI/180;
    }


    /*
    Testing two different variations of calculating
     */
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public double calculateDistance2(UserGeoLocation userGeoLocation, GeoLocation itemGeoLocation){
        double theta = userGeoLocation.getLng() - itemGeoLocation.getLng();
        double distance = Math.sin(deg2rad(userGeoLocation.getLat()))
                * Math.sin(deg2rad(itemGeoLocation.getLat()))
                + Math.cos(deg2rad(userGeoLocation.getLat()))
                *Math.cos(deg2rad(theta));

        System.out.println(distance);

        distance = Math.acos(distance);
        distance = rad2deg(distance);
        distance = distance * 60 *1.1515;

        return  distance;

    }



}

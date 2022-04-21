package com.example.idatt2106_2022_05_backend.service.ad;

import org.springframework.stereotype.Service;

@Service
public class GeoLocationService {


    /**
     * Method that calculates distance between two geolocations
     * @param lat1 latitude user
     * @param long1 longitude user
     * @param lat2 latitude item
     * @param long2 longitude item
     * @return distance in km
     */
    public double calculateDistanceInMeters(double lat1, double long1, double lat2,
                                            double long2) {

        double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
        return dist/1000;
    }



}

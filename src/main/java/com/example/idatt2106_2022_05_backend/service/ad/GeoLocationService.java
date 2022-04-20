package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

@Service
public interface GeoLocationService {


    Response calculateDistance(double lat1, double lng1, double lat2, double lng2);





}

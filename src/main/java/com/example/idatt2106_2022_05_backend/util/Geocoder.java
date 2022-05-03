package com.example.idatt2106_2022_05_backend.util;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.service.ad.AdServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Class to find an ads coordinates based on an address
 */
public class Geocoder {

    private static final String GEOCODING_RESOURCE = "https://geocode.search.hereapi.com/v1/geocode";
    private static final String API_KEY = "QPTQrOSAiHC3_HV8e13pkE57Wct8MyCiwFoTL_54_gg";

    private Logger logger = LoggerFactory.getLogger(Geocoder.class);

    public String GeocodeSync(String query) throws IOException, InterruptedException {

        HttpClient httpClient = HttpClient.newHttpClient();
        logger.info("created client");
        String encodedQuery = URLEncoder.encode(query,"UTF-8");
        String requestUri = GEOCODING_RESOURCE + "?apiKey=" + API_KEY + "&q=" + encodedQuery;
        logger.info("created query: " + requestUri);
        HttpRequest geocodingRequest = HttpRequest.newBuilder().GET().uri(URI.create(requestUri))
                .timeout(Duration.ofMillis(2000)).build();

        logger.info("created geocodingrequest");
        HttpResponse geocodingResponse = httpClient.send(geocodingRequest,
                HttpResponse.BodyHandlers.ofString());

        logger.info("Recieved response: " + geocodingResponse.body());
        return String.valueOf(geocodingResponse.body());
    }
/*
    public static void main(String[] args) throws IOException, InterruptedException {
        AdServiceImpl adService = new AdServiceImpl();
        Ad ad = new Ad();
        ad.setCity("Pozuelo de Alarcon");
        ad.setStreetAddress("C.Manuel Roses 15C");
        ad.setPostalCode(28223);
        adService.setCoordinatesOnAd(ad);
        System.out.println("This ads coordianates are: " + ad.getLat()+ " "+ ad.getLng() );

    }
 */
}
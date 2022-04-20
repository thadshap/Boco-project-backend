package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdServiceImpl {

    @Autowired
    AdRepository adRepository;

    // Get all ads
    public Response getAllAds() {
        return new Response(adRepository.findAll(), HttpStatus.OK);
    }

    // Get ad by id
    public Response getAdById(long id) {
        Optional<Ad> ad = adRepository.findById(id);
        if(ad.isPresent()) {
            return new Response(ad.get(), HttpStatus.OK);
        }
        else{
            return new Response(null,HttpStatus.NOT_FOUND);
        }
    }

    // Get all ads for user
    public Response getAllAdsByUser(long userId) {

    }
    // Get random ads --> how many? 20-50?
    // Get all available ads
    // Get all ads by postal code
    // Get all ads with items that are being given away

    // post new ad

    // update ad title
    // update ad description
    // update ad duration (how long it can be rented for)
    // update ad price
    // update ad street address
    // update ad postal code

    // delete ad
}

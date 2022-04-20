package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AdServiceImpl {

    @Autowired
    AdRepository adRepository;

    @Autowired
    UserRepository userRepository;

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
        if(userRepository.getAdsByUserId(userId) != null) {
            return new Response(userRepository.getAdsByUserId(userId), HttpStatus.OK);
        }
        else {
            return new Response(null, HttpStatus.NO_CONTENT);
        }
    }

    // Get random ads --> how many? 20-50? TODO implement in frontend

    // Get all available ads
    public Response getAllAvailableAds() {
        Set<Ad> availableAds = adRepository.getAllAvailableAds();

        // If the db contains any available ads
        if(availableAds.size() != 0) {
            return new Response(availableAds, HttpStatus.OK);
        }

        // The db did not contain any available ads
        else {
            return new Response(null, HttpStatus.NO_CONTENT);
        }
    }

    // Get all available ads by user id
    public Response getAllAvailableAdsByUser(long userId) {
        Set<Ad> availableAds = adRepository.getAvailableAdsByUserId(userId);

        // If the db contains any available ads
        if(availableAds.size() != 0) {
            return new Response(availableAds, HttpStatus.OK);
        }

        // The db did not contain any available ads
        else {
            return new Response(null, HttpStatus.NO_CONTENT);
        }
    }
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

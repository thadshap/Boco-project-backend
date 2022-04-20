package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.service.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AdController {

    @Autowired
    AdService adService;

    // get all ads
    @GetMapping("/ads")
    public Response getAllAds() {
        return adService.getAllAds();
    }

    // Get ad by id
    @GetMapping("/ads/{adId}")
    public Response getAdById(@PathVariable("adId") long id) {
        return adService.getAdById(id);
    }

    // Get all ads for user
    @GetMapping("/users/ads/{userId}")
    public Response getAdByUserId(@PathVariable("userId") long id) {
        return adService.getAllAdsByUser(id);
    }

    // Get all ads by postal code
    @PostMapping("/users/ads")
    public Response getAdByPostalCode(@RequestBody int postalCode) { // todo use dto instead?
        return adService.getAllAdsByPostalCode(postalCode);
    }

    // Get all ads by rental type
    // Post new ad
    // get all reviews for an add with owner = user id
    // update methods...

}

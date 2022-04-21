package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.service.AdService;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
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

    // Get all available ads
    @PostMapping("/ads/available")
    public Response getAllAvailableAds() {
        return adService.getAllAvailableAds();
    }

    // Get all available ads by user
    @GetMapping("/ads/available/{userId}")
    public Response getAvailableAdsByUserId(@PathVariable("userId") long id) {
        return adService.getAllAvailableAdsByUser(id);
    }

    // Get all ads by postal code
    @PostMapping("/ads")
    public Response getAdByPostalCode(@RequestBody int postalCode) { // todo use dto instead?
        return adService.getAllAdsByPostalCode(postalCode);
    }

    // Get all ads by rental type
    @PostMapping("/ads")
    public Response getAllAdsByRentalType(@RequestBody boolean rentalType) {
        return adService.getAllAdsByRentalType(rentalType);
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

    @GetMapping("/ads/nearby")
    public Response getAllAdsNeaby(@RequestBody UserGeoLocation userGeoLocation){
        return adService.getAllAdsWithDistance(userGeoLocation);
    }

    // Get all ads by rental type
    // Post new ad
    @PostMapping("/ads/newAd")
    public Response postAd(@RequestBody AdDto adDto) {
        return adService.postNewAd(adDto);
    }

    // get all reviews for an add with owner = user id
    @GetMapping("/users/ads/reviews/{userId}")
    public Response getReviewsByUserId(@PathVariable("userId") long id) {
        return adService.getReviewsByUserId(id);
    }

    // update title
    @PostMapping("/ads/updateTitle")
    public Response updateTitle(@RequestBody AdDto adDto) {
        return adService.updateTitle(adDto.getAdId(), adDto.getTitle());
    }

    // Update description
    @PostMapping("/ads/updateDescription")
    public Response updateDescription(@RequestBody AdDto adDto) {
        return adService.updateDescription(adDto.getAdId(), adDto.getDescription());
    }

    // Update duration
    @PostMapping("/ads/updateDuration")
    public Response updateDuration(@RequestBody AdDto adDto) {
        return adService.updateDuration(adDto.getAdId(), adDto.getDuration());
    }

    // Update duration type
    @PostMapping("/ads/updateDurationType")
    public Response updateDurationType(@RequestBody AdDto adDto) {
        return adService.updateDurationType(adDto.getAdId(), adDto.getDurationType());
    }

    // Update price
    @PostMapping("/ads/updatePrice")
    public Response updatePrice(@RequestBody AdDto adDto) {
        return adService.updatePrice(adDto.getAdId(), adDto.getPrice());
    }

    // Update street address
    @PostMapping("/ads/updateStreetAddress")
    public Response updateStreetAddress(@RequestBody AdDto adDto) {
        return adService.updateStreetAddress(adDto.getAdId(), adDto.getStreetAddress());
    }

    // Update postal code
    @PostMapping("/ads/updatePostalCode")
    public Response updatePostalCode(@RequestBody AdDto adDto) {
        return adService.updatePostalCode(adDto.getAdId(), adDto.getPostalCode());
    }

    // Update rented out
    @PostMapping("/ads/updateRentedOut")
    public Response updateRentedOut(@RequestBody AdDto adDto) {
        return adService.updateRentedOut(adDto.getAdId(), adDto.isRentedOut());
    }

    // Delete ad
    @DeleteMapping("ads/{adId}")
    public Response deleteAd(@PathVariable long adId) {
        return adService.deleteAd(adId);
    }
}

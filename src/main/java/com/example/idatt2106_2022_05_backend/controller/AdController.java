package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.*;
import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@Api(tags = "Controller class to manage endpoints for managing ad object")
public class AdController {

    @Autowired
    AdService adService;

    @GetMapping("/ads")
    @ApiOperation(value = "Endpoint to return all ads", response = Response.class)
    public Response getAllAds() {
        try {
            log.debug("[X] Call to return all ads");
            return adService.getAllAds();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response("Could not get ads", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/ads/available/true")
    @ApiOperation(value = "Endpoint to get avaliable ads", response = Response.class)
    public Response getAllAvailableAds() {
        log.debug("[X] Call to retrieve avaliable ads");
        return adService.getAllAvailableAds();
    }

    @GetMapping("/ads/available/{userId}")
    @ApiOperation(value = "Endpoint to get all avaliable ads by user", response = Response.class)
    public Response getAvailableAdsByUserId(@PathVariable("userId") long id) {
        log.debug("[X] Call to get all avaliable ads by user with id = {}", id);
        return adService.getAllAvailableAdsByUser(id);
    }

    @GetMapping("/ads/postal/{postalCode}")
    @ApiOperation(value = "Endpoint to return all ads by with the same postal code", response = Response.class)
    public Response getAdByPostalCode(@PathVariable int postalCode) { // todo use dto instead?
        log.debug("[X] Call to get all ads with postal code = {}", postalCode);
        return adService.getAllAdsByPostalCode(postalCode);
    }

    @GetMapping("/ads/rental/{rentalType}")
    @ApiOperation(value = "Endpoint to return all ads with specific rental type", response = Response.class)
    public Response getAllAdsByRentalType(@PathVariable boolean rentalType) {
        log.debug("[X] Call to return ads by rental type = {}", rentalType);
        return adService.getAllAdsByRentalType(rentalType);
    }

    @GetMapping("/ads/{adId}")
    @ApiOperation(value = "Endpoint to return an ad by ad id", response = Response.class)
    public Response getAdById(@PathVariable("adId") long id) {
        log.debug("[X] Call to get an ad with id = {}", id);
        return adService.getAdById(id);
    }

    @GetMapping("/users/ads/{userId}")
    @ApiOperation(value = "Endpoint to return all ads for a user", response = Response.class)
    public Response getAdByUserId(@PathVariable("userId") long id) {
        log.debug("[X] Call to return all ads for a user with id = {}", id);
        return adService.getAllAdsByUser(id);
    }

    @PostMapping("/ads/nearby")
    @ApiOperation(value = "Endpoint to return all ads near a location", response = Response.class)
    public Response getAllAdsNearby(@RequestBody UserGeoLocation userGeoLocation) throws IOException {
        log.debug("[X] Call to retrieve all ads nearby a location");
        return adService.getAllAdsWithDistance(userGeoLocation);
    }

    @PostMapping("/ads/newAd")
    @ApiOperation(value = "Endpoint to create a new ad", response = Response.class)
    public Response postAd(@RequestBody AdDto adDto) throws IOException {
        log.debug("[X] Call to create a new ad");
        return adService.postNewAd(adDto);
    }

    @GetMapping("/users/ads/reviews/{userId}")
    @ApiOperation(value = "Endpoint to retrieve all reviews on an ad of a user", response = Response.class)
    public Response getReviewsByUserId(@PathVariable("userId") long id) {
        log.debug("[X] Call to retrieve all reviews for ad by user with id = {}", id);
        return adService.getReviewsByUserId(id);
    }

    @PutMapping("/ads/{adId}")
    @ApiOperation(value = "", response = Response.class)
    public Response updateAd(@PathVariable Long adId, @RequestBody AdUpdateDto adUpdateDto) {
        log.debug("[X] Call to update an ad with id = {}", adId);
        return adService.updateAd(adId, adUpdateDto);
    }

    @DeleteMapping("ads/{adId}")
    @ApiOperation(value = "Endpoint to delete an ad", response = Response.class)
    public Response deleteAd(@PathVariable long adId) {
        log.debug("[X] Call to delete ad with id = {}", adId);
        return adService.deleteAd(adId);
    }

    @DeleteMapping("/ads/picture")
    @ApiOperation(value = "Endpoint to delete a picture from an ad", response = Response.class)
    public Response deletePicture(@RequestBody UpdatePictureDto updatePictureDto) throws IOException {
        log.debug("[X] Picture to delete from add with id = {}", updatePictureDto.getAd_id());
        return adService.deletePicture(updatePictureDto.getAd_id(), updatePictureDto.getFile().getBytes());
    }

    @PostMapping("/ads/picture")
    @ApiOperation(value = "Endpoint to add a picture an ad", response = Response.class)
    public Response uploadNewPicture(@RequestBody UpdatePictureDto updatePictureDto) throws IOException {
        log.debug("[X] Picture to added for ad with id = {}", updatePictureDto.getAd_id());
        return adService.uploadNewPicture(updatePictureDto.getAd_id(), updatePictureDto.getFile());
    }

    @GetMapping("/ads/page/{sizeOfPage}")
    @ApiOperation(value = "Endpoint to request a page of ads")
    public Response getPageOfAds(@PathVariable int sizeOfPage){
        return adService.getPageOfAds(sizeOfPage);
    }

    @PostMapping("/ads/sort/distance")
    @ApiOperation(value = "Endpoint to request an amount of ads with calculated distance")
    public Response getSortedByDistance(@RequestBody UserGeoLocation userGeoLocation) throws IOException {
        return adService.sortByDistance(userGeoLocation);
    }

    @PostMapping("/ads/sort/descending")
    @ApiOperation(value = "gets a page of given size and sorted by an attribute, descending")
    public Response getsorteddescending(@RequestBody SortingAdsDto sortingDto){
        return adService.sortByDescending(sortingDto.getPageSize(), sortingDto.getSortBy());
    }

    @PostMapping("/ads/sort/ascending")
    @ApiOperation(value = "gets a page of given size and sorted by an attribute ascending")
    public Response getsortedAscending(@RequestBody SortingAdsDto sortingDto){
        return adService.sortByAscending(sortingDto.getPageSize(), sortingDto.getSortBy());
    }

    @GetMapping("/ads/newest/{pageSize}")
    @ApiOperation(value = "sorting all ads by when they are created")
    public Response getnewest(@PathVariable int pageSize){

        return adService.sortByCreatedDateAscending(pageSize);
    }

    @GetMapping("/ads/oldest/{pageSize}")
    @ApiOperation(value = "sorting all ads by creation oldest")
    public Response getoldest(@PathVariable int pageSize){

        return adService.sortByCreatedDateDescending(pageSize);
    }

    @GetMapping("/search/{searchWord}")
    @ApiOperation(value = "method to search through")
    public Response searchInAdsAndCategories(@PathVariable String searchWord){
        return adService.searchThroughAds(searchWord);
    }


}

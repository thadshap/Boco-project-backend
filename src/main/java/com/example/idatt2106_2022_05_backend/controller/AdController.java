package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.dto.UpdatePictureDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

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
    public Response deletePicture(@ModelAttribute UpdatePictureDto updatePictureDto) throws IOException {
        log.debug("[X] Picture to delete from add with id = {}", updatePictureDto.getId());
        return adService.deletePicture(updatePictureDto.getId(), updatePictureDto.getMultipartFile().getBytes());
    }

    // Not in use
    /**
    @PostMapping("/ads/picture")
    @ApiOperation(value = "Endpoint to add a picture an ad", response = Response.class)
    public Response uploadNewPicture(@ModelAttribute UpdatePictureDto updatePictureDto) throws IOException {
        log.debug("[X] Picture to added for ad with id = {}", updatePictureDto.getId());
        return adService.uploadNewPicture(updatePictureDto.getId(), updatePictureDto.getMultipartFile());
    }
     */


    @PostMapping("/ads/newPicture")
    public Response uploadPicture(@ModelAttribute UpdatePictureDto dto) {
        try {
            return adService.storeImageForAd(dto.getId(), dto.getMultipartFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Post multiple images --> dto contains adId and file array
    @PostMapping(value = "/ads/newPictures",
                 consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
                 produces = {MediaType.APPLICATION_JSON_VALUE} )
    public Response uploadPictures(AdDto dto) {
        Set<MultipartFile> files = dto.getPictures();
        for(MultipartFile file : files) {
            try {
                adService.storeImageForAd(dto.getAdId(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Response("Pictures are saved", HttpStatus.OK);
    }

    @GetMapping("/ads/pictures/{adId}")
    public Response getPicturesForAd(@PathVariable long adId) {
        return adService.getAllPicturesForAd(adId);
    }

    @PostMapping("/ads/page")
    public Response getPageOfAds(@RequestBody AdDto sizeOfPage){
        return adService.getPageOfAds(sizeOfPage.getSizeOfPage());
    }

    // Get all categories
    @GetMapping("/categories")
    public Response getAllCategories(){
        return adService.getAllCategories();
    }

    // Get sub-categories for main category
    @GetMapping("/categories/{parentCategoryName}")
    public Response getSubCategoriesForCategory(@PathVariable String parentCategoryName){
        return adService.getAllSubCategories(parentCategoryName);
    }

    // Get all ads for specific category id
    @GetMapping("/categories/ads/{categoryId}")
    public Response getAllAdsInCategory(@PathVariable long categoryId){
        return adService.getAllAdsInCategory(categoryId);
    }

    // Get all ads in category and sub-categories and then their sub-categories etc (recursive)
    @GetMapping("/categoriesRecursive/{categoryName}")
    public Response getAllAdsInCategoryRecursively(@PathVariable String categoryName){
        return adService.getAllAdsInCategoryAndSubCategories(categoryName);
    }

    // Get all parent categories
    @GetMapping("/categories/parent")
    public Response getAllParentCategories(){
        return adService.getAllParentCategories();
    }


}

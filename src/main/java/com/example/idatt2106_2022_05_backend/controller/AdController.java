package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.*;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.dto.ad.UpdatePictureDto;
import com.example.idatt2106_2022_05_backend.dto.ad.FilterListOfAds;
import com.example.idatt2106_2022_05_backend.dto.user.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.security.SecurityService;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@Api(tags = "Controller class to manage endpoints for managing ad object")
public class AdController {

    @Autowired
    AdService adService;

    @Autowired
    SecurityService securityService;

    private Logger logger = LoggerFactory.getLogger(AdController.class);

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
    public Response getAdByPostalCode(@PathVariable int postalCode) {
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

    @PostMapping("/auth/ads/newAd")
    @ApiOperation(value = "Endpoint to create a new ad", response = Response.class)
    public Response postAd(@RequestBody AdDto adDto) throws IOException, InterruptedException {
        log.debug("[X] Call to create a new ad");
        if (!securityService.isUser(adDto.getUserId()) && !securityService.isVerifiedUser(0L)) {
            return new Response("Du kan opprette leie objektet," + " du må verifisere emailen din.",
                    HttpStatus.NO_CONTENT);
        }
        return adService.postNewAd(adDto);
    }

    @GetMapping("/users/ads/reviews/{userId}")
    @ApiOperation(value = "Endpoint to retrieve all reviews on an ad of a user", response = Response.class)
    public Response getReviewsByUserId(@PathVariable("userId") long id) {
        log.debug("[X] Call to retrieve all reviews for ad by user with id = {}", id);
        return adService.getReviewsByUserId(id);
    }

    @PutMapping("/auth/ads/{adId}")
    @ApiOperation(value = "", response = Response.class)
    public Response updateAd(@PathVariable Long adId, @RequestBody AdUpdateDto adUpdateDto) {
        log.debug("[X] Call to update an ad with id = {}", adId);
        if (!securityService.isAdOwner(adId)) {
            return new Response("Du har ikke tilgang til dette.", HttpStatus.NO_CONTENT);
        }
        return adService.updateAd(adId, adUpdateDto);
    }

    @DeleteMapping("/auth/ads/{adId}")
    @ApiOperation(value = "Endpoint to delete an ad", response = Response.class)
    public Response deleteAd(@PathVariable long adId) {
        log.debug("[X] Call to delete ad with id = {}", adId);
        if (!securityService.isAdOwner(adId)) {
            return new Response("Du har ikke tilgang.", HttpStatus.NO_CONTENT);
        }
        return adService.deleteAd(adId);
    }

    @DeleteMapping("/auth/ads/picture/{userId}")
    @ApiOperation(value = "Endpoint to delete a picture from an ad", response = Response.class)
    public Response deletePicture(@PathVariable Long userId, @ModelAttribute UpdatePictureDto updatePictureDto,
            @RequestPart List<MultipartFile> files) throws IOException {
        log.debug("[X] Picture to delete from add with id = {}", updatePictureDto.getId());
        if (!securityService.isAdOwner(userId)) {
            return new Response("Du har ikke tilgang.", HttpStatus.NO_CONTENT);
        }
        return adService.deletePicture(updatePictureDto.getId(), files);
    }

    // Not in use
    /**
     * @PostMapping("/ads/picture")
     *
     * @ApiOperation(value = "Endpoint to add a picture an ad", response = Response.class) public Response
     *                     uploadNewPicture(@ModelAttribute UpdatePictureDto updatePictureDto) throws IOException {
     *                     log.debug("[X] Picture to added for ad with id = {}", updatePictureDto.getId()); return
     *                     adService.uploadNewPicture(updatePictureDto.getId(), updatePictureDto.getMultipartFile()); }
     */

    @PostMapping(value = "/auth/ads/newPicture/{userId}/{adId}")
    @ApiOperation(value = "Endpoint to ad picture to ad", response = Response.class)
    public Response adPicture(@PathVariable Long userId, @PathVariable Long adId,
            @RequestPart List<MultipartFile> files) throws IOException {
        log.debug("[X] Picture to ad to add with id = {}", adId);
        if (!securityService.isAdOwner(userId)) {
            return new Response("Du har ikke tilgang.", HttpStatus.NO_CONTENT);
        }
        return adService.storeImageForAd(adId, files);
    }

    @GetMapping("/ads/pictures/{adId}")
    @ApiOperation(value = "Endpoint to get pictures for ad", response = Response.class)
    public List<PictureReturnDto> getPicturesForAd(@PathVariable long adId) {
        log.debug("[X] Call to get all pictures from add with id = {}", adId);
        return adService.getAllPicturesForAd(adId);
    }

    @GetMapping("/ads/picture/{adId}")
    @ApiOperation(value = "Endpoint to get first picture for ad", response = Response.class)
    public Response getPictureForAd(@PathVariable long adId) {
        log.debug("[X] Call to get first picture from add with id = {}", adId);
        return adService.getFirstPictureForAd(adId);
    }

    @PostMapping("/ads/page/{sizeOfPage}")
    @ApiOperation(value = "Endpoint to request a page of ads", response = Response.class)
    public Response getPageOfAds(@PathVariable int sizeOfPage, @RequestBody UserGeoLocation userGeoLocation) {
        log.debug("[X] Call to get page of ads");
        return adService.getPageOfAds(sizeOfPage, userGeoLocation);
    }

    @PostMapping("/search/{searchWord}")
    @ApiOperation(value = "Endpoint to search through", response = Response.class)
    public Response searchInAdsAndCategories(@PathVariable String searchWord, UserGeoLocation userGeoLocation) {
        log.debug("[X] Call to search in ads and categories");
        return adService.searchThroughAds(searchWord, userGeoLocation);
    }

    // Get all categories
    @GetMapping("/categories")
    @ApiOperation(value = "Endpoint to get all categories", response = Response.class)
    public Response getAllCategories() {
        log.debug("[X] Call to get all categories");
        return adService.getAllCategories();
    }

    // Get sub-categories for a category
    @GetMapping("/categories/{parentCategoryName}")
    @ApiOperation(value = "Endpoint to get all sub-categories for category", response = Response.class)
    public Response getSubCategoriesForCategory(@PathVariable String parentCategoryName) {
        log.debug("[X] Call to get all sub-categories for category with name = {}", parentCategoryName);
        return adService.getAllSubCategories(parentCategoryName);
    }

    // Get all ads for specific category id
    @GetMapping("/categories/ads/{categoryId}")
    @ApiOperation(value = "Endpoint to get all ads in specific category", response = Response.class)
    public Response getAllAdsInCategory(@PathVariable long categoryId) {
        log.debug("[X] Call to get all ads in category with id = {}", categoryId);
        return adService.getAllAdsInCategory(categoryId);
    }

    // Get all ads in category and sub-categories and then their sub-categories etc (recursive)
    @PostMapping("/categoriesRecursive/{categoryName}")
    @ApiOperation(value = "Endpoint to get all ads in specified category recursively", response = Response.class)
    public Response getAllAdsInCategoryRecursively(@PathVariable String categoryName,
            @RequestBody UserGeoLocation userGeoLocation) {
        log.debug("[X] Call to get all ads in category with name = {}", categoryName);
        return adService.getAllAdsInCategoryAndSubCategories(categoryName, userGeoLocation);
    }

    @GetMapping("/categories/level")
    @ApiOperation(value = "Endpoint to get all ads in category level", response = Response.class)
    public Response getAllAdsInCategoryRecursively() {
        log.debug("[X] Call to get all ads in category level");
        return adService.getAllCategoriesWithLevel();
    }

    // Get all parent categories
    @GetMapping("/categories/parent")
    @ApiOperation(value = "Endpoint to get all parent categories", response = Response.class)
    public Response getAllParentCategories() {
        log.debug("[X] Call to get all parent categories");
        return adService.getAllParentCategories();
    }

    // Get all ads in city
    @GetMapping("/ads/city/{cityName}")
    @ApiOperation(value = "Endpoint to get all ads in given city", response = Response.class)
    public Response getAllAdsInCity(@PathVariable String cityName) {
        log.debug("[X] Call to get all ads in city with name = {}", cityName);
        return adService.getAllAdsInCity(cityName);
    }

    @PostMapping("/ads/filter")
    @ApiOperation(value = "Endpoint to filter ads with given filters", response = Response.class)
    public Response filterAds(@RequestBody FilterListOfAds filterListOfAds) {
        log.debug("[X] Call to filter ads with type = {}", filterListOfAds.getFilterType());
        return adService.getAllAdsWithFilter(filterListOfAds);
    }

    @PostMapping("/ads/category/filter")
    @ApiOperation(value = "Endpoint to get ads with category and filter", response = Response.class)
    public Response getAdsWithCategoryAndFilter(@RequestBody FilterListOfAds filterListOfAds) {
        log.debug("[X] Call to filter ads with type = {}", filterListOfAds.getFilterType());
        return adService.getAdsWithCategoryAndFilter(filterListOfAds);
    }

}

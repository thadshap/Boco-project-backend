package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.PixelGrabber;
import java.io.IOException;

@Service
public interface AdService {
    // Get all ads
    Response getAllAds() throws IOException;

    // Get all ads in category by category id
    Response getAllAdsInCategory(Long categoryId);

    // Get all ads in category by category name
    Response getAllAdsInCategory(String name);

    Response getAllAdsInCategoryAndSubCategories(String name);

    Response getAllParentCategories();

    // Get ad by id
    Response getAdById(long id);

    // Get all ads for user
    Response getAllAdsByUser(long userId);

    // Get all available ads
    Response getAllAvailableAds();

    // Get all available ads by user id
    Response getAllAvailableAdsByUser(long userId);

    //Get a page of ads
    Response getPageOfAds(int sizeOfPage);

    // Get all ads by postal code
    Response getAllAdsByPostalCode(int postalCode);

    // Get all ads by rental type
    Response getAllAdsByRentalType(boolean rentalType);

    Response getAllAdsInCity(String city);

    Response postNewAd(AdDto adDto) throws IOException;

    // get all reviews for an add with owner = user id
    Response getReviewsByUserId(long userId);

    // update ad title
    Response updateAd(Long adId, AdUpdateDto adUpdateDto);

    Response getAllAdsWithDistance(UserGeoLocation userGeoLocation) throws IOException;
    // delete ad

    Response deleteAd(long adId);
    //delete picture

    Response deletePicture(long ad_id, byte[] chosenPicture);

    // Response uploadNewPicture(long ad_id, MultipartFile file) throws IOException;

    // Response uploadPictureToAd(long adId, MultipartFile file);

    Response getAllPicturesForAd(long adId);

    Response storeImageForAd(long adId, MultipartFile file) throws IOException;

    Response getAllSubCategories(String parentName);

    Response getAllCategories();
}


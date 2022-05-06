package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.ad.FilterListOfAds;
import com.example.idatt2106_2022_05_backend.dto.PictureReturnDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface AdService {
    // Get all ads
    Response getAllAds() throws IOException;

    // Get all ads in category by category id
    Response getAllAdsInCategory(Long categoryId);

    // Get all ads in category by category name
    Response getAllAdsInCategory(String name);

    Response getAllAdsInCategoryAndSubCategories(String name, UserGeoLocation userGeoLocation);

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
    Response getPageOfAds(int sizeOfPage, UserGeoLocation userGeoLocation);

    // Get all ads by postal code
    Response getAllAdsByPostalCode(int postalCode);

    // Get all ads by rental type
    Response getAllAdsByRentalType(boolean rentalType);

    // post new add
    Response getAllAdsInCity(String city);

    Response postNewAd(AdDto adDto) throws IOException, InterruptedException;

    // get all reviews for an add with owner = user id
    Response getReviewsByUserId(long userId);

    // update ad title
    Response updateAd(Long adId, AdUpdateDto adUpdateDto);

    // getting all ads with calculated distance
    Response getAllAdsWithDistance(UserGeoLocation userGeoLocation) throws IOException;

    // delete ad
    Response deleteAd(long adId);

    // delete picture
    Response deletePicture(long ad_id, List<MultipartFile> chosenPicture) throws IOException;

    Response getAllCategoriesWithLevel();

    // Search in database
    Response searchThroughAds(String searchword, UserGeoLocation userGeoLocation);

    Response storeImageForAd(long adId, List<MultipartFile> file) throws IOException;

    List<PictureReturnDto> getAllPicturesForAd(long adId);

    Response getFirstPictureForAd(long adId);

    Response getAllSubCategories(String parentName);

    Response getAllCategories();

    Response getAllAdsWithFilter(FilterListOfAds filterListOfAds);

    Response getAdsWithCategoryAndFilter(FilterListOfAds filterListOfAds);

}

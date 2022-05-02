package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.PixelGrabber;
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

    //post new add
    Response getAllAdsInCity(String city);

    Response postNewAd(AdDto adDto) throws IOException, InterruptedException;

    // get all reviews for an add with owner = user id
    Response getReviewsByUserId(long userId);

    // update ad title
    Response updateAd(Long adId, AdUpdateDto adUpdateDto);

    //getting all ads with calculated distance
    Response getAllAdsWithDistance(UserGeoLocation userGeoLocation) throws IOException;

    // delete ad
    Response deleteAd(long adId);

    //delete picture
    Response deletePicture(long ad_id, byte[] chosenPicture);

    //upload a new picture
    // Response uploadNewPicture(long ad_id, MultipartFile file) throws IOException;

    //get nearest ads
    Response sortByDistance(UserGeoLocation userGeoLocation) throws IOException;

    //generic sorting descending
    Response sortByDescending(int pageSize, String sortBy);

    //generic sort ascending
    Response sortByAscending(int pageSize, String sortBy);

    //get newest ads
    Response sortByCreatedDateAscending(int pageSize);

    //get oldest ads
    Response sortByCreatedDateDescending(int pageSize);

    //Search in database
    Response searchThroughAds(String searchword);

    //Sort an array by price
    Response sortArrayByPriceAscending(List<AdDto> list);

    //Sort an array by price descending
    Response sortArrayByPriceDescending(List<AdDto> list);

    //Sort an array based on distance ascending
    Response sortArrayByDistanceAscending(List<AdDto> list);

    //Sort an array based on distance descending
    Response sortArrayByDistanceDescending(List<AdDto> list);

    //Getting ads only within a distance intervall
    Response getListWithinDistanceIntervall(List<AdDto> list, double limit);

    //Getting all ads within a priceRange
    Response getListOfAdsWithinPriceRange(List<AdDto> list, double upperLimit, double lowerLimit);
    // Response uploadNewPicture(long ad_id, MultipartFile file) throws IOException;

    // Response uploadPictureToAd(long adId, MultipartFile file);

    Response getAllPicturesForAd(long adId);

    Response storeImageForAd(long adId, MultipartFile file) throws IOException;

    Response getAllSubCategories(String parentName);

    Response getAllCategories();

    Response sortArrayOfAdsByDateNewestFirst(List<AdDto> list);

    Response sortArrayOfAdsByDateOldestFirst(List<AdDto> list);

    //Ad setLagLongFromAdress(Ad ad) throws IOException, InterruptedException;
}


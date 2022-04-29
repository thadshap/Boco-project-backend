package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
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

    // Get ad by id
    Response getAdById(Long id);

    // Get all ads for user
    Response getAllAdsByUser(Long userId);

    // Get all available ads
    Response getAllAvailableAds();

    // Get all available ads by user id
    Response getAllAvailableAdsByUser(Long userId);

    //Get a page of ads
    Response getPageOfAds(int sizeOfPage);

    // Get all ads by postal code
    Response getAllAdsByPostalCode(int postalCode);

    // Get all ads by rental type
    Response getAllAdsByRentalType(boolean rentalType);

    //post new add
    Response postNewAd(AdDto adDto) throws IOException;

    // get all reviews for an add with owner = user id
    Response getReviewsByUserId(Long userId);

    // update ad title
    Response updateAd(Long adId, AdUpdateDto adUpdateDto);

    //getting all ads with calculated distance
    Response getAllAdsWithDistance(UserGeoLocation userGeoLocation) throws IOException;

    // delete ad
    Response deleteAd(Long adId);

    //delete picture
    Response deletePicture(Long ad_id, byte[] chosenPicture);

    //upload a new picture
    Response uploadNewPicture(Long ad_id, MultipartFile file) throws IOException;

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
}


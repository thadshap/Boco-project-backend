package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.AdDto;
import com.example.idatt2106_2022_05_backend.dto.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface AdService {
    // Get all ads
    Response getAllAds();

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
    Response postNewAd(AdDto adDto) throws IOException;

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
    Response uploadNewPicture(long ad_id, MultipartFile file) throws IOException;

    //get nearest ads
    Response sortByDistance(UserGeoLocation userGeoLocation, int amountOfAds) throws IOException;

    //generic sorting descending
    Response sortByDescending(int pageSize, String sortBy);

    //generic sort ascending
    Response sortByAscending(int pageSize, String sortBy);

    //get newest ads
    Response sortByCreatedDateAscending(int pageSize);

    //get oldest ads
    Response sortByCreatedDateDescending(int pageSize);
}


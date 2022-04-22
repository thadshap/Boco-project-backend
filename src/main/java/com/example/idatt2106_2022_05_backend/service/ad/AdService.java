package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.AdDto;
import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    // Get all ads by postal code
    Response getAllAdsByPostalCode(int postalCode);

    // Get all ads by rental type
    Response getAllAdsByRentalType(boolean rentalType);

    Response postNewAd(AdDto adDto) throws IOException;

    // get all reviews for an add with owner = user id
    Response getReviewsByUserId(long userId);

    // update ad title
    Response updateTitle(long adId, String newTitle);

    // update ad description
    Response updateDescription(long adId, String newTitle);

    // update ad duration (how long it can be rented for)
    Response updateDuration(long adId, int newDuration);

    // update duration-type
    Response updateDurationType(long adId, AdType newDurationType);

    // update ad price
    Response updatePrice(long adId, int newPrice);

    // update ad street address
    Response updateStreetAddress(long adId, String newStreetAddress);

    // update ad postal code
    Response updatePostalCode(long adId, int newPostalCode);

    // update ad rented_out status...
    Response updateRentedOut(long adId, boolean rentedOut);

    Response getAllAdsWithDistance(UserGeoLocation userGeoLocation) throws IOException;

    // delete ad
    Response deleteAd(long adId);

    //delete picture
    public Response deletePicture(long ad_id, long picture_id);
}


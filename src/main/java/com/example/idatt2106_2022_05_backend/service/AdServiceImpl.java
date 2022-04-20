package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.dto.AdDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AdServiceImpl {

    @Autowired
    AdRepository adRepository;

    @Autowired
    UserRepository userRepository;

    // Get all ads
    public Response getAllAds() {
        return new Response(adRepository.findAll(), HttpStatus.OK);
    }

    // Get ad by id
    public Response getAdById(long id) {
        Optional<Ad> ad = adRepository.findById(id);
        if(ad.isPresent()) {
            return new Response(ad.get(), HttpStatus.OK);
        }
        else{
            return new Response(null,HttpStatus.NOT_FOUND);
        }
    }

    // Get all ads for user
    public Response getAllAdsByUser(long userId) {
        if(userRepository.getAdsByUserId(userId) != null) {
            return new Response(userRepository.getAdsByUserId(userId), HttpStatus.OK);
        }
        else {
            return new Response(null, HttpStatus.NO_CONTENT);
        }
    }

    // Get random ads --> how many? 20-50? TODO implement in frontend

    // Get all available ads
    public Response getAllAvailableAds() {
        Set<Ad> availableAds = adRepository.getAllAvailableAds();

        // If the db contains any available ads
        if(availableAds.size() != 0) {
            return new Response(availableAds, HttpStatus.OK);
        }

        // The db did not contain any available ads
        else {
            return new Response(null, HttpStatus.NO_CONTENT);
        }
    }

    // Get all available ads by user id
    public Response getAllAvailableAdsByUser(long userId) {
        Set<Ad> availableAds = adRepository.getAvailableAdsByUserId(userId);

        // If the db contains any available ads
        if(availableAds.size() != 0) {
            return new Response(availableAds, HttpStatus.OK);
        }

        // The db did not contain any available ads
        else {
            return new Response(null, HttpStatus.NO_CONTENT);
        }
    }

    // Get all ads by postal code
    public Response getAllAdsByPostalCode(int postalCode) {
        return new Response(adRepository.findByPostalCode(postalCode), HttpStatus.OK);
    }

    /**
     * Get all ads with items that are:
     *      - Being given away = false
     *      - Being rented out = true
      */
    //
    public Response getAllAdsByRentalType(boolean rentalType) {
        Set<Ad> ads = adRepository.findByRental(rentalType);

        if(ads != null) {
            return new Response(ads, HttpStatus.OK)
        }
        else {
            return new Response(null, HttpStatus.NO_CONTENT);
        }
    }


    /**
     * Posts new ad
     * @param adDto must contain:
     *              - rental (being rented out or given away)
     *              - rentedOut (true if the item is rented out)
     *              - duration (quantity of duration type)
     *              - durationType (type of duration --> see "AdType" enum)
     *              - categoryId (only the id of the nearest category)
     *              - price
     *              - street_address (of the item)
     *              - postal_code (of the item)
     *              - name (header of the ad)
     *
     *              can contain (nullable in db):
     *              - description
     *              - picture (pictures of the item to be rented out)
     *
     * @return
     */
    public Response postNewAd(AdDto adDto) {
        Ad newAd = new Ad();

        // Checking if dto contains any of the nullable attributes
        if(adDto.getDescription() != null) {
            newAd.setDescription(adDto.getDescription());
        }
        if(adDto.getPicturesIn() != null) {
            // todo fix this
        }



    }

    // update ad title
    // update ad description
    // update ad duration (how long it can be rented for)
    // update ad price
    // update ad street address
    // update ad postal code
    // update ad rented_out status...

    // delete ad
}

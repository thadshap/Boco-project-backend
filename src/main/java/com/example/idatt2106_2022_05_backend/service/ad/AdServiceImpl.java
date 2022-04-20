package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.AdDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CategoryRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AdServiceImpl implements AdService {

    @Autowired
    AdRepository adRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    // Get all ads
    @Override
    public Response getAllAds() {
        return new Response(adRepository.findAll(), HttpStatus.OK);
    }

    // Get ad by id
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public Response getAllAdsByPostalCode(int postalCode) {
        return new Response(adRepository.findByPostalCode(postalCode), HttpStatus.OK);
    }

    /**
     * Get all ads with items that are:
     *      - Being given away = false
     *      - Being rented out = true
      */
    // Get all ads by rental type
    @Override
    public Response getAllAdsByRentalType(boolean rentalType) {
        Set<Ad> ads = adRepository.findByRental(rentalType);

        if(ads != null) {
            return new Response(ads, HttpStatus.OK);
        }
        else {
            return new Response(null, HttpStatus.NO_CONTENT);
        }
    }


    /**
     * Posts new ad
     * @param adDto must contain:
     *              - rental (being rented out or given away)
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
     *              - rentedOut (true if the item is rented out, which it should be at initialization)
     *
     * @return
     */
    @Override
    public Response postNewAd(AdDto adDto) {
        Ad newAd = new Ad();

        // Required attributes
        newAd.setRental(adDto.isRental());
        newAd.setRentedOut(false);
        newAd.setDuration(adDto.getDuration());
        newAd.setDurationType(adDto.getDurationType());
        newAd.setPrice(adDto.getPrice());
        newAd.setStreetAddress(adDto.getStreetAddress());
        newAd.setTitle(adDto.getTitle());

        // If category exists
        Optional<Category> category = categoryRepository.findById(adDto.getCategoryId());
        if(category.isPresent()) {
            newAd.setCategory(category.get());
        }
        // If the category given is null or wrong, the ad cannot be created
        else {
            return new Response(null, HttpStatus.NOT_ACCEPTABLE);
        }

        // Checking if dto contains any of the nullable attributes
        if(adDto.getDescription() != null) {
            newAd.setDescription(adDto.getDescription());
        }
        if(adDto.getPicturesIn() != null) {
            // todo fix this
        }

    }

    // get all reviews for an add with owner = user id
    @Override
    public Response getReviewsByUserId(long userId) {

        // Set containing all reviews connected to the ads that the given user owns
        Set<Review> reviews = adRepository.getReviewsByUserId(userId);

        // If the reviews-list contains anything
        if(adRepository.getReviewsByUserId(userId) != null) {
            return new Response(adRepository.getReviewsByUserId(userId), HttpStatus.OK);
        }
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }
    }

    // update ad title
    @Override
    public Response updateTitle(long adId, String newTitle) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If ad exists
        if(ad.isPresent()) {

            // Update the ad
            ad.get().setTitle(newTitle);

            // Save the changes
            adRepository.save(ad.get());

            // HttpStatus = OK
            return new Response(null, HttpStatus.OK);
        }

        // The given ad id was not present in db
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }
    }

    // update ad description
    @Override
    public Response updateDescription(long adId, String newTitle) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If ad exists
        if(ad.isPresent()) {

            // Update the ad
            ad.get().setTitle(newTitle);

            // Save the changes
            adRepository.save(ad.get());

            // HttpStatus = OK
            return new Response(null, HttpStatus.OK);
        }

        // The given ad id was not present in db
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }

    }

    // update ad duration (how long it can be rented for)
    @Override
    public Response updateDuration(long adId, int newDuration) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If ad exists
        if(ad.isPresent()) {

            // Update the ad
            ad.get().setDuration(newDuration);

            // Save the changes
            adRepository.save(ad.get());

            // HttpStatus = OK
            return new Response(null, HttpStatus.OK);
        }

        // The given ad id was not present in db
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }

    }

    // update duration-type
    @Override
    public Response updateDurationType(long adId, AdType newDurationType) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If ad exists
        if(ad.isPresent()) {

            // Update the ad
            ad.get().setDurationType(newDurationType);

            // Save the changes
            adRepository.save(ad.get());

            // HttpStatus = OK
            return new Response(null, HttpStatus.OK);
        }

        // The given ad id was not present in db
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }

    }

    // update ad price
    @Override
    public Response updatePrice(long adId, int newPrice) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If ad exists
        if(ad.isPresent()) {

            // Update the ad
            ad.get().setPrice(newPrice);

            // Save the changes
            adRepository.save(ad.get());

            // HttpStatus = OK
            return new Response(null, HttpStatus.OK);
        }

        // The given ad id was not present in db
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }

    }

    // update ad street address
    @Override
    public Response updateStreetAddress(long adId, String newStreetAddress) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If ad exists
        if(ad.isPresent()) {

            // Update the ad
            ad.get().setStreetAddress(newStreetAddress);

            // Save the changes
            adRepository.save(ad.get());

            // HttpStatus = OK
            return new Response(null, HttpStatus.OK);
        }

        // The given ad id was not present in db
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }

    }

    // update ad postal code
    @Override
    public Response updatePostalCode(long adId, int newPostalCode) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If ad exists
        if(ad.isPresent()) {

            // Update the ad
            ad.get().setPostalCode(newPostalCode);

            // Save the changes
            adRepository.save(ad.get());

            // HttpStatus = OK
            return new Response(null, HttpStatus.OK);
        }

        // The given ad id was not present in db
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }

    }

    // update ad rented_out status...
    @Override
    public Response updateRentedOut(long adId, boolean rentedOut) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If ad exists
        if(ad.isPresent()) {

            // Update the ad
            ad.get().setRentedOut(rentedOut);

            // Save the changes
            adRepository.save(ad.get());

            // HttpStatus = OK
            return new Response(null, HttpStatus.OK);
        }

        // The given ad id was not present in db
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }

    }

    // delete ad
    @Override
    public Response deleteAd(long adId) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If the ad exists
        if(ad.isPresent()) {

            // Delete the ad
            adRepository.deleteById(adId);

            // HttpResponse = OK
            return new Response(null, HttpStatus.OK);
        }
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }
    }
}

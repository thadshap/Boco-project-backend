package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.dto.AdDto;
import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CategoryRepository;
import com.example.idatt2106_2022_05_backend.repository.PictureRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.PictureUploadResponse;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AdServiceImpl {

    @Autowired
    AdRepository adRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PictureRepository pictureRepository;

    private ModelMapper modelMapper = new ModelMapper();

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
    public Response postNewAd(AdDto adDto) throws IOException {
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
            //Creating and saving each picture connected to ad
            for(MultipartFile m : adDto.getPicturesIn()){
                savePicture(m, newAd);
            }
        }
        return new Response(null, HttpStatus.OK);
    }

    /*
    support method to create and save Picture
     */
    private void savePicture(MultipartFile file, Ad ad) throws IOException {
        if(file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Picture file is empty");
        }
        Picture picture = new Picture();
        picture.setFilename(file.getOriginalFilename());
        picture.setType(file.getContentType());
        picture.setContent(PictureUtility.compressImage(file.getBytes()));
        picture.setAd(ad);
    }

    public Response getAllAdsWithDistance(UserGeoLocation userGeoLocation) throws IOException {
        ArrayList<AdDto> ads = new ArrayList<>();
        for(Ad ad :adRepository.findAll()){
            //Setting all attributes and decompressing pictures in help method
           AdDto adDto = castObject(ad);
           //Calculate and set distance
           //TODO: calculate distance
           //Adding all ads to list and then response
           ads.add(adDto);
        }
        return new Response(ads, HttpStatus.OK);
    }

    private AdDto castObject(Ad ad) throws IOException {
        AdDto adDto = new AdDto();
        adDto.setDescription(ad.getDescription());
        adDto.setCategoryId(ad.getAdId());
        adDto.setDuration(ad.getDuration());
        adDto.setDurationType(ad.getDurationType());
        adDto.setPostalCode(ad.getPostalCode());
        adDto.setPrice(ad.getPrice());
        adDto.setStreetAddress(ad.getStreetAddress());
        adDto.setTitle(ad.getTitle());

        //decompressing and converting images in support method
        convertPictures(ad, adDto);
        return adDto;
    }

    private void convertPictures(Ad ad, AdDto adDto) throws IOException {
        ArrayList<Picture> pictures = ad.getPictures();
        ArrayList<Image> images = adDto.getPicturesOut();
        for(Picture picture : pictures){
            ByteArrayInputStream bis = new ByteArrayInputStream(PictureUtility.decompressImage(picture.getContent()));
            Image image = ImageIO.read(bis);
            images.add(image);
        }
        adDto.setPicturesOut(images);

    }

    /**
     * Method that calculates distance between two geolocations
     * @param lat1 latitude user
     * @param long1 longitude user
     * @param lat2 latitude item
     * @param long2 longitude item
     * @return distance in km
     */
    public Response calculateDistance(double lat1, double long1, double lat2,
                                      double long2) {

        double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
        return new Response(dist/1000, HttpStatus.OK);
    }

    // get all reviews for an add with owner = user id

    // update ad title
    // update ad description
    // update ad duration (how long it can be rented for)
    // update ad price
    // update ad street address
    // update ad postal code
    // update ad rented_out status...

    // delete ad
}

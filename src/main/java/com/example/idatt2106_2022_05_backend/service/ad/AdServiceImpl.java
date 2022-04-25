package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.AdDto;
import com.example.idatt2106_2022_05_backend.dto.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CategoryRepository;
import com.example.idatt2106_2022_05_backend.repository.PictureRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    PictureRepository pictureRepository;

    private ModelMapper modelMapper = new ModelMapper();



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

    /**
     * Get a page of ads
     * @param sizeOfPage number of the page size
     * @return Response with page in body
     */
    @Override
    public Response getPageOfAds(int sizeOfPage){
        Pageable pageOf24 = PageRequest.of(0,sizeOfPage);
        List<Ad> ads = adRepository.findAll(pageOf24).getContent();
        return new Response(ads, HttpStatus.OK);
    }

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
            return new Response("could not find category", HttpStatus.NOT_FOUND);
        }
        //Getting user
        Optional<User> user = Optional.ofNullable(userRepository.findById(adDto.getUser_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find user")));
        //checking user
        user.ifPresent(newAd::setUser);
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
        //ensures that content of file is presentent
        if(file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Picture file is empty");
        }
        //create and save object
        pictureRepository.save(Picture.builder()
        .type(file.getContentType())
        .filename(file.getOriginalFilename())
        .ad(ad).content(PictureUtility.compressImage(file.getBytes())).build());
    }

    /**
     * method that goes through all ads and returns the with the calculated distance
     * @param userGeoLocation users location
     * @return a list of ads including the distance to the users location
     * @throws IOException if decompression pictures fails
     */
    public Response getAllAdsWithDistance(UserGeoLocation userGeoLocation) throws IOException {
        ArrayList<AdDto> ads = new ArrayList<>();
        for(Ad ad :adRepository.findAll()){
            //Setting all attributes and decompressing pictures in help method
            AdDto adDto = castObject(ad);
            //Calculate and set distance
            adDto.setDistance(calculateDistance(userGeoLocation.getLat(), userGeoLocation.getLng(), ad.getLat(), ad.getLng()));
            //Adding all ads to list and then response
            ads.add(adDto);
        }
        return new Response(ads, HttpStatus.OK);
    }

    /**
     * support method that creates a dto of ad
     * @param ad ad
     * @return ad dto
     * @throws IOException if decompression of bytes fails
     */
    private AdDto castObject(Ad ad) throws IOException {
        AdDto adDto = new AdDto(); // todo use builder/modelMapper
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

    /**
     * support method to decompress pictures
     * @param ad ad object from database
     * @param adDto dto object to be returned
      * @throws IOException if decompression fails
     */
    private void convertPictures(Ad ad, AdDto adDto) throws IOException {
        Set<Picture> pictures = ad.getPictures();
        Set<Image> images = adDto.getPicturesOut();
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
    public double calculateDistance(double lat1, double long1, double lat2,
                                      double long2) {

        double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
        return dist/1000;
    }

    // get all reviews for an add with owner = user id
    @Override
    public Response getReviewsByUserId(long userId) {

        // If the reviews-list contains anything
        if(adRepository.getReviewsByUserId(userId) != null) {
            return new Response(adRepository.getReviewsByUserId(userId), HttpStatus.OK);
        }
        else {
            return new Response(null, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Response updateAd(Long adId, AdUpdateDto adUpdateDto) {
        Optional<Ad> adOptional = adRepository.findById(adId);
        Ad ad;
        if(adOptional.isPresent()) {
            ad = adOptional.get();
            // Update the ad
            if (!adUpdateDto.getTitle().isBlank()){
                ad.setTitle(adUpdateDto.getTitle());
            }
            if (!adUpdateDto.getDescription().isBlank()){
                ad.setDescription(adUpdateDto.getDescription());
            }
            if (adUpdateDto.getDuration() > 0){
                ad.setDuration(adUpdateDto.getDuration());
            }
            if (adUpdateDto.getDurationType() != null){
                ad.setDurationType(adUpdateDto.getDurationType());
            }
            if (adUpdateDto.getPrice() > 0){
                ad.setPrice(adUpdateDto.getPrice());
            }
            if (!adUpdateDto.getStreetAddress().isBlank()){
                ad.setStreetAddress(adUpdateDto.getStreetAddress());
            }
            if (adUpdateDto.getPostalCode() > 0){
                ad.setPostalCode(adUpdateDto.getPostalCode());
            }
            if(!adUpdateDto.getRentedOut().isBlank()){
                if (!adUpdateDto.getRentedOut().equalsIgnoreCase("true")){
                    ad.setRentedOut(false);
                }
                if (!adUpdateDto.getRentedOut().equalsIgnoreCase("false")){
                    ad.setRentedOut(true);
                }
            }
            adRepository.save(ad);
        }
        else {
            return new Response("Ad was not found in the database", HttpStatus.NOT_FOUND);
        }
        return new Response("Ad is updated", HttpStatus.OK);
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

    /**
     * method to delete a picture on an ad
     * @param ad_id ad_id
     * @param chosenPicture picture_id
     * @return response with status ok or not found
     */
    @Override
    public Response deletePicture(long ad_id, byte[] chosenPicture){
        Ad ad = adRepository.getById(ad_id);
        List<Picture> picture = pictureRepository.findByAd(ad);
        if(picture!=null) {
            for (Picture p : picture) {
                if (PictureUtility.decompressImage(p.getContent()).equals(chosenPicture)) {
                    pictureRepository.delete(p);
                    return new Response(null, HttpStatus.OK);
                }
            }
        }
        return new Response(null, HttpStatus.NOT_FOUND);
    }

    /**
     * method to ad a new  picture to an ad
     * @param ad_id ad_id
     * @param file file containing picture
     * @return response with status ok
     * @throws IOException if compression of file fails
     */
    @Override
    public Response uploadNewPicture(long ad_id, MultipartFile file) throws IOException {
        //Getting the ad to connect to the picture
        Ad ad = adRepository.getById(ad_id);
        //building and saving the picture
        pictureRepository.save(Picture.builder()
        .filename(file.getOriginalFilename())
        .ad(ad).type(file.getContentType()).content(PictureUtility.compressImage(file.getBytes())).build());
        return new Response(null, HttpStatus.OK);
    }


}

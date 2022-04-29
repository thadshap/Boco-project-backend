package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class to handle Ad objects
 */
@Service
public class AdServiceImpl implements AdService {

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PictureRepository pictureRepository;

    private ModelMapper modelMapper = new ModelMapper();


    /**
     * Method to get all ads.
     * @return List of all ads.
     * @throws IOException if reading of image fails.
     */
    @Override
    public Response getAllAds() throws IOException {
        List<Ad> allAds = adRepository.findAll();

        List<AdDto> adsToBeReturned = new ArrayList<>();

        // Iterate over all ads and create dtos
        for(Ad ad : allAds) {
            AdDto newAd = castObject(ad);
            adsToBeReturned.add(newAd);
        }

        return new Response(adsToBeReturned, HttpStatus.OK);
    }

    /**
     * Method to get all ads in a category.
     * @param categoryId category if.
     * @return list of Ads in a category.
     */
    @Override
    public Response getAllAdsInCategory(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);

        // If category exists
        if(category.isPresent()) {
            Set<Ad> adsFound = category.get().getAds();

            // Return the ads
            return new Response(adsFound, HttpStatus.OK);
        }
        else {
            return new Response("Fant ikke kategorien", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Method to get all ads in category by category name.
     * @param name name of the category.
     * @return all ads in a category.
     */
    @Override
    public Response getAllAdsInCategory(String name) {
        Optional<Category> category = categoryRepository.findByName(name);

        // If category exists
        if(category.isPresent()) {
            Set<Ad> adsFound = category.get().getAds();

            // Return the ads
            return new Response(adsFound, HttpStatus.OK);
        }
        else {
            return new Response("Fant ikke kategorien", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Method to get ad by id
     * @param id id of the Ad.
     * @return
     */
    @Override
    public Response getAdById(Long id) {
        List<AdDto> ads = adRepository.findById(id).stream()
                .map(adDto -> modelMapper.map(adDto, AdDto.class)).collect(Collectors.toList());
        if(ads.size()!=0) {
            return new Response(ads, HttpStatus.OK);
        }
        else{
            return new Response("Fant ikke annonser i databasen",HttpStatus.NOT_FOUND);
        }
    }

    // Get all ads for user
    @Override
    public Response getAllAdsByUser(Long userId) {
        if(userRepository.getAdsByUserId(userId) != null) {
            return new Response(userRepository.getAdsByUserId(userId).stream()
                    .map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList()), HttpStatus.OK);
        }
        else {
            return new Response("Fant ingen annonser på brukeren", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Get a page of ads
     * @param sizeOfPage number of the page size
     * @return Response with page in body
     */
    @Override
    public Response getPageOfAds(int sizeOfPage){
        Pageable pageOf = PageRequest.of(0,sizeOfPage);
        List<AdDto> ads = adRepository.findAll(pageOf).stream()
                .map( ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList());
        return new Response(ads, HttpStatus.OK);
    }

    /**
     * Method to get all available ads
     * @return all ads that are available.
     */
    @Override
    public Response getAllAvailableAds() {
        List<AdDto> availableAds = adRepository.getAllAvailableAds().stream()
                .map(ad -> modelMapper.map(ad,AdDto.class)).collect(Collectors.toList());

        // If the db contains any available ads
        if(availableAds.size() != 0) {
            return new Response(availableAds, HttpStatus.OK);
        }

        // The db did not contain any available ads
        else {
            return new Response("Fant ingen annonser", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Method to get all available ads by user id.
     * @param userId id of user.
     * @return all ads available by user.
     */
    @Override
    public Response getAllAvailableAdsByUser(Long userId) {
        List<AdDto> availableAds = adRepository.getAvailableAdsByUserId(userId).stream()
                .map(ad ->modelMapper.map(ad, AdDto.class)).collect(Collectors.toList());

        // If the db contains any available ads
        if(availableAds.size() != 0) {
            return new Response(availableAds, HttpStatus.OK);
        }
        // The db did not contain any available ads
        else {
            return new Response("Could not find any available ads for that user", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Get all ads by postal code.
     * @param postalCode postal code to get ads by.
     * @return all ads in a given postal code.
     */
    @Override
    public Response getAllAdsByPostalCode(int postalCode) {
        Set<Ad> availableAds = adRepository.findByPostalCode(postalCode);

        ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

        // Iterate over all ads and create dtos
        for(Ad ad : availableAds) {

            AdDto newAd = null;
            try {
                newAd = castObject(ad);
            } catch (IOException e) {
                e.printStackTrace();
            }

            adsToBeReturned.add(newAd);
        }

        // If the db contains any available ads
        if(availableAds.size() != 0) {
            return new Response(adsToBeReturned, HttpStatus.OK);
        }
        else {
            return new Response("Could not find any available ads", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all ads with items that are:
     * - Being given away = false
     * - Being rented out = true
     * @param rentalType being given away.
     * @return all ads by rental type.
     */
    @Override
    public Response getAllAdsByRentalType(boolean rentalType) {
        Set<AdDto> ads = adRepository.findByRental(rentalType).stream()
                .map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toSet());

        if(ads.size() != 0) {
            return new Response(ads, HttpStatus.OK);
        }
        else {
            return new Response("Could not find ads", HttpStatus.NO_CONTENT);
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
     * @return response
     */
    @Override
    public Response postNewAd(AdDto adDto) throws IOException {
        Ad newAd = new Ad();

        // Required attributes
        newAd.setRental(adDto.isRental());
        newAd.setRentedOut(false);
        newAd.setDuration(adDto.getDuration());
        newAd.setDurationType(adDto.getDurationType()); //todo check
        newAd.setPrice(adDto.getPrice());
        newAd.setStreetAddress(adDto.getStreetAddress());
        newAd.setTitle(adDto.getTitle());
        newAd.setPostalCode(adDto.getPostalCode());

        // If category exists
        Optional<Category> category = categoryRepository.findById(adDto.getCategoryId());
        if(category.isPresent()) {
            newAd.setCategory(category.get());
        }
        // If the category given is null or wrong, the ad cannot be created
        else {
            return new Response("Fant ikke kategorien", HttpStatus.NOT_FOUND);
        }

        Optional<User> user = userRepository.findById(adDto.getUserId());

        if(user.isPresent()) {
            // Set foreign key
            newAd.setUser(user.get());
        }
        else {
            return new Response("could not find user", HttpStatus.NOT_FOUND);
        }
        /**
        //Getting user
        Optional<User> user = Optional.ofNullable(userRepository.findById(adDto.getUser_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fant ikke brukeren")));
        //checking user
        Optional<User> user = Optional.ofNullable(userRepository.findById(adDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find user")));

        // Checking user
        user.ifPresent(newAd::setUser);
         */

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

        // Persisting the entities
        adRepository.save(newAd);
        user.get().setAd(newAd);
        userRepository.save(user.get());

        return new Response("everything went well", HttpStatus.OK);
    }

    /*
        support method to create and save Picture
     */
    private Response savePicture(MultipartFile file, Ad ad) throws IOException {

        // Ensures that content of file is present
        if(file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Bildefilen er tom");
        }

        // Ensure that the ad exists
        Optional<Ad> adFound = adRepository.findById(ad.getId());

        if(adFound.isPresent()) {

            // Create picture object
            Picture picture = Picture.builder()
                    .type(file.getContentType())
                    .filename(file.getOriginalFilename())
                    .ad(ad).content(PictureUtility.compressImage(file.getBytes())).build();

            // Save picture object
            pictureRepository.save(picture);

            // Add picture object as foreign key to the ad
            adFound.get().getPictures().add(picture);

            // Persist ad
            adRepository.save(adFound.get());

            // Return proper response
            return new Response("Bildet ble lagret", HttpStatus.OK);
        }
        return new Response("Fant ikke annonsen", HttpStatus.NOT_FOUND);
    }

    /**
     * method that goes through all ads and returns the with the calculated distance
     * @param userGeoLocation users location
     * @return a list of ads including the distance to the users location
     * @throws IOException if decompression pictures fails
     */
    public Response getAllAdsWithDistance(UserGeoLocation userGeoLocation) throws IOException {
        ArrayList<AdDto> ads = new ArrayList<>();

        for(Ad ad : adRepository.findAll()){
            //Setting all attributes and decompressing pictures in help method
            AdDto adDto = castObject(ad);
            //Calculate and set distance
            adDto.setDistance(calculateDistance(userGeoLocation.getLat(),
                    userGeoLocation.getLng(), ad.getLat(), ad.getLng()));
            //Adding all ads to list and then response
            ads.add(adDto);
        }
        return new Response(ads.stream().sorted(Comparator.comparing(AdDto::getDistance))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * support method that creates a dto of ad
     * @param ad ad
     * @return ad dto
     * @throws IOException if decompression of bytes fails
     */
    private AdDto castObject(Ad ad) throws IOException {
        AdDto adDto = modelMapper.map(ad, AdDto.class);

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

    /**
     * Method to get all reviews for an add with owner = user id.
     * @param userId id of user.
     * @return all reviews by user id
     */
    @Override
    public Response getReviewsByUserId(Long userId) {

        // If the reviews-list contains anything
        if(adRepository.getReviewsByUserId(userId) != null) {
            return new Response(adRepository.getReviewsByUserId(userId).stream().map(review -> modelMapper
                    .map(review, ReviewDto.class)).collect(Collectors.toList()), HttpStatus.OK);
        }
        else {
            return new Response("Fant ingen omtaler på denne brukeren", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Method to update an ad.
     * @param adId id of ad to update.
     * @param adUpdateDto Ad dto to update.
     * @return response.
     */
    @Override
    public Response updateAd(Long adId, AdUpdateDto adUpdateDto) {

        Optional<Ad> foundAd = adRepository.findById(adId);

        if(foundAd.isPresent()) {
            Ad ad = foundAd.get();

            // Update the ad
            if (adUpdateDto.getTitle() != null){
                ad.setTitle(adUpdateDto.getTitle());
            }
            if (adUpdateDto.getDescription() != null){
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
            if (adUpdateDto.getStreetAddress() != null){
                ad.setStreetAddress(adUpdateDto.getStreetAddress());
            }
            if (adUpdateDto.getPostalCode() > 0){
                ad.setPostalCode(adUpdateDto.getPostalCode());
            }
//            if(adUpdateDto.getRentedOut() != null){
//                if (!adUpdateDto.getRentedOut().equalsIgnoreCase("true")){
//                    ad.setRentedOut(false);
//                }
//                if (!adUpdateDto.getRentedOut().equalsIgnoreCase("false")){
//                    ad.setRentedOut(true);
//                }
//            }
            adRepository.save(ad);
        }
        else {
            return new Response("Fant ikke annonsen", HttpStatus.NOT_FOUND);
        }
        return new Response("Annonsen er oppdatert", HttpStatus.OK);
    }

    /**
     * Method to delete an ad.
     * @param adId id of ad to delete.
     * @return response.
     */
    @Override
    public Response deleteAd(Long adId) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If the ad exists
        if(ad.isPresent()) {

            // Delete the ad's pictures
            ad.get().setPictures(null);

            // Delete the ad from its category
            ad.get().getCategory().getAds().remove(ad.get());

            // Delete the ad from its user
            ad.get().getUser().getAds().remove(ad.get());

            // Delete its rentals
            ad.get().setRentals(null);

            // Delete the reviews todo save these somewhere else during next iteration!
            ad.get().setReviews(null);

            // Delete the dates
            ad.get().setDates(null);

            // Delete the ad
            adRepository.deleteById(adId);

            // HttpResponse = OK
            return new Response("Annonsen er slettet", HttpStatus.OK);
        }
        else {
            return new Response("Fant ikke annonsen", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * method to delete a picture on an ad
     * @param ad_id ad_id
     * @param chosenPicture picture_id
     * @return response with status ok or not found
     */
    @Override
    public Response deletePicture(Long ad_id, byte[] chosenPicture){
        Optional<Ad> ad = adRepository.findById(ad_id);

        // If present
        if(ad.isPresent()) {
            List<Picture> pictures = pictureRepository.findByAd(ad.get());
            if(pictures != null) {
                for (Picture picture : pictures) {
                    if (Arrays.equals(PictureUtility.decompressImage(picture.getContent()), chosenPicture))
                    {
                        pictureRepository.delete(picture);
                        return new Response("Slettet bildet", HttpStatus.OK);
                    }
                }
            }
        }

        return new Response("Bildet ble ikke funnet i databasen", HttpStatus.NOT_FOUND);
    }

    /**
     * method to add a new  picture to an ad
     * @param ad_id ad_id
     * @param file file containing picture
     * @return response with status ok
     * @throws IOException if compression of file fails
     */
    @Override
    public Response uploadNewPicture(Long ad_id, MultipartFile file) throws IOException {

        //Getting the ad to connect to the picture
        Optional<Ad> ad = adRepository.findById(ad_id);

        if(ad.isPresent()) {

            //building and saving the picture
            pictureRepository.save(Picture.builder()
                    .filename(file.getOriginalFilename())
                    .ad(ad.get()).type(file.getContentType()).
                    content(PictureUtility.compressImage(file.getBytes())).build());

            // Return OK response
            return new Response("Bildet ble lagret", HttpStatus.OK);
        }

        // The ad was not found
        return new Response("Annonsen ble ikke funnet", HttpStatus.NOT_FOUND);
    }
    /**
     * Method to get ads sorted on distance to user
     * @param userGeoLocation users location
     * @return list of ads
     * @throws IOException exception
     */
    @Override
    public Response sortByDistance(UserGeoLocation userGeoLocation) throws IOException {
        List<AdDto> ads = (List<AdDto>) getAllAdsWithDistance(userGeoLocation).getBody();
        return new Response(ads.stream().limit(userGeoLocation.getAmount()).collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * sorting method descending
     * @param pageSize page size
     * @param sortBy sorting by attribute
     * @return response
     */
    @Override
    public Response sortByDescending(int pageSize, String sortBy){
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(sortBy).descending());
        List<Ad> list =  adRepository.findAll(pageable).get().collect(Collectors.toList());
        return new Response(list.stream()
                .map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList()), HttpStatus.OK);

    }

    /**
     * sorting method ascending
     * @param pageSize page size
     * @param sortBy sort by attribute
     * @return response
     */
    @Override
    public Response sortByAscending(int pageSize, String sortBy){
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(sortBy).ascending());
        List<Ad> list = adRepository.findAll(pageable).get().collect(Collectors.toList());
        return new Response(list.stream()
                .map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * method to get newest ads
     * @param pageSize page size
     * @return response with list
     */
    @Override
    public Response sortByCreatedDateAscending(int pageSize){
        List<Ad> ads = adRepository.findAll();
        ads.sort(Comparator.comparing(Ad::getCreated));
        return new Response(ads.stream()
                .map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList()).stream()
                .limit(pageSize), HttpStatus.OK);
    }

    /**
     * method to get oldest ads
     * @param pageSize page size
     * @return response with list
     */
    @Override
    public Response sortByCreatedDateDescending(int pageSize){
        List<Ad> ads = adRepository.findAll();
        ads.sort(Comparator.comparing(Ad::getCreated).reversed());
        return new Response(ads.stream()
                .map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList()).stream()
                .limit(pageSize), HttpStatus.OK);
    }

    /**
     * Method to search through ads.
     * @param searchword search word.
     * @return ads by a search word.
     */
    @Override
    public Response searchThroughAds(String searchword){
        List<Ad> ads = new ArrayList<>();
        Set<Ad> ad = adRepository.findByTitleContaining(searchword);
        List<Category> categories = categoryRepository.findByNameContaining(searchword);

        //Adding all ads with the category
        for(Category c: categories){
            for(Ad a: c.getAds()){
                ads.add(a);
            }
        }

        //Adding all ads with the searchword in the title
        for(Ad a: ad){
            if(!ads.contains(a)){
                ads.add(a);
            }
        }
        //Casting objects to Dto and returning
        return new Response(ads.stream()
                .map(ad1 -> modelMapper.map(ad1, AdDto.class)).collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * Method to sort ads by ascending price.
     * @param list list of ads to sort.
     * @return sorted ads
     */
    @Override
    public Response sortArrayByPriceAscending(List<AdDto> list){
        list.sort(Comparator.comparing(AdDto::getPrice));
        return new Response(list, HttpStatus.OK);
    }

    /**
     * Method to sort ads by descending price.
     * @param list list of ads to sort.
     * @return sorted ads.
     */
    @Override
    public Response sortArrayByPriceDescending(List<AdDto> list){
        list.sort(Comparator.comparing(AdDto::getPrice).reversed());
        return new Response(list, HttpStatus.OK);
    }

    /**
     * Method to sort ads by distance ascending price.
     * @param list list of ads to sort.
     * @return sorted ads.
     */
    @Override
    public Response sortArrayByDistanceAscending(List<AdDto> list){
        list.sort(Comparator.comparing(AdDto::getDistance));
        return new Response(list, HttpStatus.OK);
    }

    /**
     * Method to sort ads by distance descending price.
     * @param list list of ads to sort.
     * @return sorted ads.
     */
    @Override
    public Response sortArrayByDistanceDescending(List<AdDto> list){
        list.sort(Comparator.comparing(AdDto::getDistance).reversed());
        return new Response(list, HttpStatus.OK);
    }

    /**
     * Method to sort ads by distance interval price.
     * @param list list of ads to sort.
     * @param limit distance difference.
     * @return sorted ads.
     */
    @Override
    public Response getListWithinDistanceIntervall(List<AdDto> list, double limit){
        list.stream().filter(x -> x.getDistance()<limit).collect(Collectors.toList());
        return new Response(list, HttpStatus.OK);
    }

    /**
     * Method to sort ads by distance interval price.
     * @param list list of ads to sort.
     * @param upperLimit upper limit  price.
     * @param lowerLimit  lower limit  price.
     * @return sorted ads.
     */
    @Override
    public Response getListOfAdsWithinPriceRange(List<AdDto> list, double upperLimit, double lowerLimit){
        list.stream().filter(x->lowerLimit<x.getPrice() && x.getPrice()<upperLimit).collect(Collectors.toList());
        return new Response(list, HttpStatus.OK);
    }
}

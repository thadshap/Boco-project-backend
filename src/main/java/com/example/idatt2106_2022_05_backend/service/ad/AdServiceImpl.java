package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.CategoryDto;
import com.example.idatt2106_2022_05_backend.dto.PictureDto;
import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.util.FileUploadUtility;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.List;

@Service
public class AdServiceImpl implements AdService {

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CalendarDateRepository calendarDateRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private PictureUtility pictureService;

    private ModelMapper modelMapper = new ModelMapper();


    // Get all ads
    @Override
    public Response getAllAds() throws IOException {
        List<Ad> allAds = adRepository.findAll();

        List<AdDto> adsToBeReturned = new ArrayList<>();

        // Iterate over all ads and create DTOs
        for(Ad ad : allAds) {
            AdDto newAd = castObject(ad);
            adsToBeReturned.add(newAd);
        }

        return new Response(adsToBeReturned, HttpStatus.OK);
    }

    // Get all ads in category by category name
    @Override
    public Response getAllAdsInCategory(String name) {
        Optional<Category> category = categoryRepository.findByName(name);

        List<AdDto> adsToReturn = new ArrayList<>();

        // If category exists
        if(category.isPresent()) {
            Set<Ad> adsFound = category.get().getAds();

            for(Ad ad : adsFound) {
                try {
                    AdDto newDto = castObject(ad);
                    newDto.setLat(ad.getLat());
                    newDto.setLng(ad.getLng());
                    adsToReturn.add(newDto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Return the ads
            return new Response(adsToReturn, HttpStatus.OK);
        }
        else {
            return new Response("Could not find category", HttpStatus.NOT_FOUND);
        }
    }

    // Get all ads in category by category id
    @Override
    public Response getAllAdsInCategory(Long categoryId)  {
        Optional<Category> category = categoryRepository.findById(categoryId);

        // List to return
        ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

        // If category exists
        if(category.isPresent()) {
            Set<Ad> adsFound = category.get().getAds();

            for(Ad ad : adsFound) {
                try {
                    // Convert to dto
                    AdDto dto = castObject(ad);
                    dto.setLat(ad.getLat());
                    dto.setLng(ad.getLng());
                    // Add dto to list of ads to be returned
                    adsToBeReturned.add(dto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Return the adDto-list
            return new Response(adsToBeReturned, HttpStatus.OK);
        }
        else {
            return new Response("Could not find specified category", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Response getAllSubCategories(String parentName) {
        // List that will be returned
        ArrayList<CategoryDto> subCategories = new ArrayList<>();

        // Retrieve all categories from database
        List<Category> categories = categoryRepository.findAll();

        // Iterate over all categories
        for(Category category : categories) {

            // Ensure null-safety by skipping the category if it does not have a parent
            if(category.getParentName() != null) {

                // Using equals w/ignore case() to ensure equality
                if(parentName.equalsIgnoreCase(category.getParentName())) {

                    // Generate a new list that holds only the ids --> avoids recursive stackOverflow
                    ArrayList<Long> ids = new ArrayList<>();

                    // If this category has any ads
                    if(category.getAds().size() > 0) {
                        for(Ad ad: category.getAds()) {
                            ids.add(ad.getId());
                        }
                    }

                    // Create dto
                    CategoryDto dto = CategoryDto.builder().
                            id(category.getId()).
                            name(category.getName()).
                            parentName(parentName).
                            adIds(ids).
                            build();


                    // Add to list of sub-categories to return
                    subCategories.add(dto);
                }
            }
        }

        // Return the list if any subcategories were added
        if(subCategories.size() > 0) {
            return new Response(subCategories, HttpStatus.OK);
        }
        // Return NOT_FOUND if there
        else {
            return new Response("No sub categories found with the specified parent-name",
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Response getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        // List that will be returned
        ArrayList<CategoryDto> categoriesToReturn = new ArrayList<>();

        // Create all dto
        for(Category category : categories) {

            // Generate a new list that holds only the ids --> avoids recursive stackOverflow
            ArrayList<Long> ids = new ArrayList<>();

            // If this category has any ads
            if(category.getAds().size() > 0) {
                for(Ad ad: category.getAds()) {
                    ids.add(ad.getId());
                }
            }

            // Create dto
            CategoryDto dto = CategoryDto.builder().
                    id(category.getId()).
                    name(category.getName()).
                    parentName(category.getParentName()).
                    adIds(ids).
                    build();


            // Add to list of sub-categories to return
            categoriesToReturn.add(dto);
        }

        // Return the list if any subcategories were added
        if(categoriesToReturn.size() > 0) {
            return new Response(categoriesToReturn, HttpStatus.OK);
        }
        // Return NOT_FOUND if there
        else {
            return new Response("No categories found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves the ads of this category and all categories that have this category as parentCategory
     *
     * @param name is the name of this category
     * @return a list of ads
     */
    @Override
    public Response getAllAdsInCategoryAndSubCategories(String name) {
        System.out.println("number of categories:" + categoryRepository.findAll().size());

        System.out.println("number of categories:" + categoryRepository.findAll().size());

        // Retrieve all categories from database
        ArrayList<Category> categories = (ArrayList<Category>) categoryRepository.findAll();

        // List of subCategories found using recursive function
        List<Category> subCategories = findSubCategories(categories, new ArrayList<>(),
                             name,0);

        System.out.println("sub categories found size: " + subCategories.size());

        ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

        // Iterate over all sub-categories found
        for(Category category : subCategories) {
            // Iterate over all ads in category
            if(category.getAds() != null) {
                for(Ad ad : category.getAds()) {
                    try {
                        // Create dto
                        AdDto dto = castObject(ad);
                        // Add to list
                        adsToBeReturned.add(dto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // Now all ads are returned
        return new Response(adsToBeReturned, HttpStatus.OK);
    }


    /**
     * Recursive function that finds all sub-categories belonging
     * to a category (including the sub-categories of sub-categories and so on)
     *
     * @param listIn is a list containing all categories in db
     * @param listOut is an empty list that is being filled up with sub-categories
     *                as the method recursively iterates
     * @param start is a measure of incrementation-depth that ends when recursions == listIn.size()
     * @return listOut
     *
    private List<Category> findAllSubCategories(ArrayList<Category> listIn, ArrayList<Category> listOut, int start) {
        // Base-case
        int arrayLength = start;

        // If the position in the array is equal to the size of the array we are at the end
        if(arrayLength == listIn.size()) {
            // Return the list that now contains all sub-categories
            return listOut;
        }
        else{
            // get a hold of all subcategories
            String nameOfCurrentCategory = listIn.get(arrayLength).getName();

            // Iterate through all categories
            for(Category category : listIn) {
                if(category.getParentName() != null) {
                    // If a category has current category as parent category
                    if(category.getParentName().equalsIgnoreCase(nameOfCurrentCategory)) {
                        listOut.add(category);
                    }
                }
            }
        }
        // Increment the starting point from the list
        return findSubCategories(listIn, listOut, start + 1);
    }
    */

    /**
     * Recursive function that finds all sub-categories belonging
     * to a category (including the sub-categories of sub-categories and so on)
     *
     * @param listIn is a list containing all categories in db
     * @param listOut is an empty list that is being filled up with sub-categories
     *                as the method recursively iterates
     * @return listOut
     */
    private List<Category> findSubCategories(ArrayList<Category> listIn,
                                                       ArrayList<Category> listOut,
                                                       String parentName, int start) {

        // Position in array == start
        int arrayLength = start;

        // Make a counter and if it is not == 1 && base case is not reached when the loop ends,
        // call on the function again from parentName == arrayLength.getName
        int loopCounter = 0;

        // Base case: If the position in the array is equal to the size of the array
        if(arrayLength == listIn.size()) {
            System.out.println("Array length equals list in --> finished");
            // Return the list that now contains all sub-categories
            return listOut;
        }
        else{
            // Iterate through all categories
            for (int i = start; i < listIn.size(); i++) {
                Category category = listIn.get(i);

                // If the category is a sub-class
                if(category.getParentName() != null) {

                    // If a category has current category as parent category
                    if(category.getParentName().equalsIgnoreCase(parentName)) {

                        // Add the category to the list to be returned
                        listOut.add(category);

                        // This category is now the new parent
                        parentName = category.getName();

                        // Call on the function recursively from the start for this category
                        findSubCategories(listIn,listOut, parentName,
                                start);
                    }
                }
                System.out.println("parent name is null");
            }
            // Increment the list and call on the function recursively
            return findSubCategories(listIn,listOut, parentName, start);
        }
    }

    @Override
    public Response getAllParentCategories() {
        List<Category> allCategories = categoryRepository.findAll();
        List<CategoryDto> categoriesToReturn = new ArrayList<>();

        for(Category category : allCategories) {
            if (category.isParent()) {
                CategoryDto dto = CategoryDto.builder().
                        id(category.getId()).
                        name(category.getName()).
                        build();
                categoriesToReturn.add(dto);
            }
        }
        if(categoriesToReturn.size() > 0) {
            // Return all the DTOs
            return new Response(categoriesToReturn,HttpStatus.OK);
        }
        else {
            return new Response("Could not find any parent categories", HttpStatus.NOT_FOUND);
        }
    }

    // Get ad by id
    @Override
    public Response getAdById(long id) {
        Optional<Ad> ad = adRepository.findById(id);
        if(ad.isPresent()) {
            try {
                // Create dto
                AdDto newDto = castObject(ad.get());

                // Return dto
                return new Response(newDto, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            return new Response("Could not find ad with specified id",HttpStatus.NOT_FOUND);
        }
        return null;
    }

    // Get all ads for user
    @Override
    public Response getAllAdsByUser(long userId) {
        Set<Ad> adsFound = userRepository.getAdsByUserId(userId);

        if(adsFound != null) {
            List<AdDto> adsToBeReturned = new ArrayList<>();

            // Create dtos by iterating over all ads and creating DTOs
            for(Ad ad : adsFound) {
                AdDto newAd = null;
                try {
                    newAd = castObject(ad);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                adsToBeReturned.add(newAd);
            }


            return new Response(adsToBeReturned, HttpStatus.OK);
        }
        else {
            return new Response("Could not find ads for specified user", HttpStatus.NO_CONTENT);
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
        List<Ad> ads = adRepository.findAll(pageOf).getContent();

        // Create dto
        ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

        // Iterate over all ads and create dtos
        for(Ad ad : ads) {

            try {
                AdDto newAd = castObject(ad);
                newAd.setLat(ad.getLat());
                newAd.setLng(ad.getLng());
                adsToBeReturned.add(newAd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new Response(adsToBeReturned , HttpStatus.OK);
    }

    // Get all available ads
    @Override
    public Response getAllAvailableAds() {
        Set<Ad> availableAds = adRepository.getAllAvailableAds();
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

        // The db did not contain any available ads
        else {
            return new Response("Could not find any available ads", HttpStatus.NO_CONTENT);
        }
    }

    // Get all available ads by user id
    @Override
    public Response getAllAvailableAdsByUser(long userId) {
        if(userRepository.existsById(userId)) {
            Set<Ad> availableAds = adRepository.getAvailableAdsByUserId(userId);

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

            // The db did not contain any available ads
            else {
                return new Response("Could not find any available ads for that user", HttpStatus.NO_CONTENT);
            }
        }
        else{
            throw new NoSuchElementException();
        }

    }

    // Get all ads by postal code
    @Override
    public Response getAllAdsByPostalCode(int postalCode) {
        Set<Ad> availableAds = adRepository.findByPostalCode(postalCode);

        ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

        // Iterate over all ads and create dtos
        for(Ad ad : availableAds) {
            try {
                AdDto newAd = castObject(ad);
                adsToBeReturned.add(newAd);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
     *      - Being given away = false
     *      - Being rented out = true
     */
    // Get all ads by rental type
    @Override
    public Response getAllAdsByRentalType(boolean rentalType) {
        Set<Ad> ads = adRepository.findByRental(rentalType);

        ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

        // Iterate over all ads and create dtos
        for(Ad ad : ads) {

            AdDto newAd = null;
            try {
                newAd = castObject(ad);
            } catch (IOException e) {
                e.printStackTrace();
            }

            adsToBeReturned.add(newAd);
        }

        return new Response(adsToBeReturned, HttpStatus.OK);
    }

    // TODO
    public Response getAllAdsInCity(String city) {

        // Retrieve all ads with specified city
        Set<Ad> adsFound = adRepository.findByCity(city);

        // Create list of DTOs to return
        Set<AdDto> adsToReturn = new HashSet<>();

        if(adsFound != null) {
            for(Ad ad : adsFound) {
                // Cast to DTO
                try {
                    AdDto newDto = castObject(ad);
                    adsToReturn.add(newDto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Return list of DTOs
            return new Response(adsToReturn, HttpStatus.OK);
        }
        // If no ads were found in city
        return new Response("No ads found in specified city", HttpStatus.NOT_FOUND);
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
        newAd.setPostalCode(adDto.getPostalCode());
        newAd.setCity(adDto.getCity());

        // If category exists
        Optional<Category> category = categoryRepository.findById(adDto.getCategoryId());
        if(category.isPresent()) {
            newAd.setCategory(category.get());
        }
        // If the category given is null or wrong, the ad cannot be created
        else {
            return new Response("could not find category", HttpStatus.NOT_FOUND);
        }

        Optional<User> user = userRepository.findById(adDto.getUserId());

        if(user.isPresent()) {
            // Set foreign key
            newAd.setUser(user.get());
        }
        else {
            return new Response("could not find user", HttpStatus.NOT_FOUND);
        }

        // Checking if dto contains any of the nullable attributes
        if(adDto.getDescription() != null) {
            newAd.setDescription(adDto.getDescription());
        }

        // Persisting the entities
        adRepository.save(newAd);
        user.get().setAd(newAd);
        userRepository.save(user.get());

        return new Response(newAd.getId(), HttpStatus.CREATED);
    }

    /**
     * Support-method to create and save Picture
     *
    private Response savePicture(MultipartFile file, Ad ad) throws IOException {

        // Ensures that content of multipartFile is present
        if(file.isEmpty()){
            return new Response("Picture multipartFile is empty", HttpStatus.NO_CONTENT);
        }

        // Ensure that the ad exists
        Optional<Ad> adFound = adRepository.findById(ad.getId());

        if(adFound.isPresent()) {

            // Create picture object
            Picture picture = Picture.builder()
                    .type(file.getContentType())
                    .filename(file.getOriginalFilename())
                    .ad(ad).data(PictureUtility.compressImage(file.getBytes())).build();

            // Save picture object
            pictureRepository.save(picture);

            // Add picture object as foreign key to the ad
            adFound.get().getPictures().add(picture);

            // Persist ad
            adRepository.save(adFound.get());

            // Return proper response
            return new Response("Picture saved", HttpStatus.OK);
        }
        return new Response("Ad not found", HttpStatus.NOT_FOUND);
    }
     */

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
        return new Response(ads, HttpStatus.OK);
    }

    /**
     * support method that creates a dto of ad
     * @param ad ad
     * @return ad dto
     * @throws IOException if decompression of bytes fails
     */
    private AdDto castObject(Ad ad) throws IOException {
        AdDto adDto = new AdDto();
        adDto.setDescription(ad.getDescription());
        adDto.setCategoryId(ad.getCategory().getId());
        adDto.setDuration(ad.getDuration());
        adDto.setDurationType(ad.getDurationType());
        adDto.setPostalCode(ad.getPostalCode());
        adDto.setCity(ad.getCity());
        adDto.setPrice(ad.getPrice());
        adDto.setStreetAddress(ad.getStreetAddress());
        adDto.setTitle(ad.getTitle());

        // decompressing and converting images in support method
        // convertPictures(ad, adDto);
        return adDto;
    }

    /**
     * support method to decompress pictures
     * @param ad ad object from database
     * @param adDto dto object to be returned
     * @throws IOException if decompression fails
     *
    private void convertPictures(Ad ad, AdDto adDto) throws IOException {
        Set<Picture> pictures = ad.getPictures();

        Set<Image> images = adDto.getPicturesOut();
        for(Picture picture : pictures){
            ByteArrayInputStream bis = new ByteArrayInputStream(PictureUtility.decompressImage(picture.getData()));
            Image image = ImageIO.read(bis);
            images.add(image);
        }
        adDto.setPicturesOut(images);
    }
    */

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

        Set<Review> reviews = adRepository.getReviewsByUserId(userId);
        Set<ReviewDto> dtos = new HashSet<>();
        for(Review review : reviews) {
            ReviewDto newDto = new ReviewDto();

            newDto.setDescription(review.getDescription());
            newDto.setRating(review.getRating());

            dtos.add(newDto);
        }

        // If the reviews-list contains anything
        if(adRepository.getReviewsByUserId(userId) != null) {
            return new Response(dtos, HttpStatus.OK);
        }
        else {
            return new Response("The user was not found", HttpStatus.NOT_FOUND);
        }
    }

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
            if(adUpdateDto.getCity() != null) {
                ad.setCity(adUpdateDto.getCity());
            }

            if (adUpdateDto.getRentedOut() == true){
                ad.setRentedOut(false);
            }
            if (adUpdateDto.getRentedOut() == false){
                ad.setRentedOut(true);
            }

            adRepository.save(ad);
        }
        else {
            return new Response("Ad was not found in the database", HttpStatus.NOT_FOUND);
        }
        return new Response("Ad updated", HttpStatus.OK);
    }

    // delete ad
    @Override
    public Response deleteAd(long adId) {
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

            Set<Review> reviews = ad.get().getReviews();

            if(reviews != null) {
                for(Review review : reviews) {
                    review.setAd(null);
                    reviewRepository.save(review);
                }
            }


            // Delete the reviews todo save these somewhere else during next iteration!

            ad.get().setReviews(null);

            // Delete the ad from the dates
            for(CalendarDate date : ad.get().getDates()) {
                date.getAds().remove(ad.get());
            }

            // Delete the dates from the ad
            ad.get().setDates(null);

            // Delete the ad
            adRepository.deleteById(adId);

            // HttpResponse = OK
            return new Response("Ad deleted", HttpStatus.OK);
        }
        else {
            return new Response("Ad not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * method to delete a picture on an ad
     * @param ad_id id
     * @param chosenPicture picture_id
     * @return response with status ok or not found
     */
    @Override
    public Response deletePicture(long ad_id, byte[] chosenPicture){
        Optional<Ad> ad = adRepository.findById(ad_id);

        // If present
        if(ad.isPresent()) {
            Set<Picture> pictures = ad.get().getPictures();
            if(pictures != null) {
                for (Picture picture : pictures) {
                    if(Arrays.equals(picture.getData(), chosenPicture)) {
                        // Remove this picture from ad
                        ad.get().getPictures().remove(picture);

                        // Set the foreign keys of the picture equal to null
                        picture.setAd(null);
                        picture.setUser(null);

                        // Delete the ad
                        pictureRepository.delete(picture);

                        // Update the ad
                        adRepository.save(ad.get());

                        return new Response("Deleted picture", HttpStatus.OK);
                    }
                }
            }
            // If we get here, pictures are equal to null
            return new Response("This ad has no pictures", HttpStatus.NOT_FOUND);
        }

        return new Response("Ad with specified id not found", HttpStatus.NOT_FOUND);
    }

    /**
     * method to add a new picture to an ad
     * @param adId id
     * @param //file multipartFile containing picture
     * @return response with status ok
     * @throws IOException if compression of multipartFile fails
     *
    @Override
    public Response uploadNewPicture(long adId, MultipartFile file) throws IOException {

        //Getting the ad to connect to the picture
        Optional<Ad> ad = adRepository.findById(adId);

        if(ad.isPresent()) {

            //building and saving the picture
            pictureRepository.save(Picture.builder()
                    .filename(file.getOriginalFilename())
                    .ad(ad.get()).type(file.getContentType()).
                    data(PictureUtility.compressImage(file.getBytes())).build());

            // Return OK response
            return new Response("Picture saved", HttpStatus.OK);
        }
        else {
            // The ad was not found
            return new Response("Ad not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
    @Override
    public Response uploadPictureToAd(long adId, MultipartFile multipartFile){
        // Get the filename
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        // Get the ad
        Optional<Ad> ad = adRepository.findById(adId);

        String uploadDirectory = "";

        if(ad.isPresent()) {

            // Give the picture object to the ad
            ad.get().setPhotos(fileName);

            // Persist the change
            Ad savedAd = adRepository.save(ad.get());

            // The upload directory is ad-photos, and the id is to create the specific file
            uploadDirectory = "src/main/resources/ad-photos/" + savedAd.getId();

            try {
                // Save the file
                FileUploadUtility.saveFile(uploadDirectory, fileName, multipartFile);

                // Return OK if the file was saved successfully
                return new Response("Photo saved", HttpStatus.OK);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // If we get here, the ad was not found
            return new Response("Ad with specified ad id not found", HttpStatus.NOT_FOUND);
        }
        return null;
    }

    */

    @Override
    public Response getAllPicturesForAd(long adId) {
        Optional<Ad> adFound = adRepository.findById(adId);

        if(adFound.isPresent()) {

            // Retrieve the pictures this ad has
            Set<Picture> pictures = adFound.get().getPictures();

            // Create a list to hold the DTOs
            Set<PictureDto> picturesToReturn = new HashSet<>();

            // If the ad has any pictures
            if(pictures != null) {

                // Iterate over the pictures
                for(Picture picture : pictures) {
                    // Create a picture dto
                    PictureDto dto = PictureDto.builder().
                            adId(picture.getId()).
                            data(picture.getData()).
                            type(picture.getType()).build();

                    // Add to DTO-list
                    picturesToReturn.add(dto);
                }
            }
            return new Response(picturesToReturn, HttpStatus.OK);
        }
        // If the ad was not found
        else {
            return new Response("There was no ad with specified id in db.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Response storeImageForAd(long adId, MultipartFile file) throws IOException {
        return pictureService.savePicture(file, adId, 0);
    }

    public Response getPicture(long pictureId) {
        Optional<Picture> picture = pictureRepository.findById(pictureId);

        if(picture.isPresent()) {
            return new Response(picture.get(), HttpStatus.OK);
        }
        else {
            return new Response("Could not find picture with specified id", HttpStatus.NOT_FOUND);
        }
    }

    /**
    public Response getAllPicturesForAd(long adId) {
        Optional<Ad> ad = adRepository.findById(adId);

        if(ad.isPresent()) {
            return new Response(ad.get().getPictures().stream(), HttpStatus.OK);
        }
        else {
            return new Response("Could not find ad with specified id", HttpStatus.NOT_FOUND);
        }
    }
     */
}

package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.CategoryDto;
import com.example.idatt2106_2022_05_backend.dto.PictureDto;
import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CategoryRepository;
import com.example.idatt2106_2022_05_backend.repository.PictureRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.Geocoder;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import com.example.idatt2106_2022_05_backend.util.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
    private RentalRepository rentalRepository;

    @Autowired
    private PictureUtility pictureService;

    private ModelMapper modelMapper = new ModelMapper();

    private Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);
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
            return new Response("Fant ikke kategorien", HttpStatus.NOT_FOUND);
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
                    HttpStatus.NO_CONTENT);
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
            return new Response("Fant ikke kategorien", HttpStatus.NOT_FOUND);
        }
    }


    /**
     * Get each category and their subCategories.
     * A new list is created for each parent.
     * Each parent's child also is iterated over
     *
    public void getCategoryHierarchies() {
        List<Category> allCategories = categoryRepository.findAll();
        List<Category> mainCategories = new ArrayList<>();

         // List of parents contains list of children which contains list of children's children
        List<List<List<Category>>> listsToReturn = new ArrayList<>();

        int placementOfParent = 0;

        // Find all the parent categories
        for(Category category : allCategories) {
            if(category.isParent()) {
                mainCategories.add(category);
            }
            placementOfParent ++;
        }

        // From main categories, find all the children
        for(Category parentCategory : mainCategories) {
            // Create a list for each category
            ArrayList<Category> children = new ArrayList<>();
            // Add the list to the list of all lists
            listsToReturn.add(children);

            // Iterate over all categories for each parentCategory
            for (Category aCategory : allCategories) {
                // Create a list for each sub-category
                ArrayList<Category> childrenOfChildren = new ArrayList<>();

                // Add the list to the list of all lists
                listsToReturn.add(children);
                // Counter to retrieve the placement in list of lists (due to enhanced for loop)
                placementOfCurrentList ++;
                // If the category is not a parent
                if(!aCategory.isParent()) {
                    // If the parentName of the category is the parentCategory
                    if(aCategory.getParentName().equalsIgnoreCase(parentCategory.getName())) {
                        // Add the category to the parent's list of children (inside the list of lists)
                        listsToReturn.get(placementOfCurrentList - 1).add(aCategory);
                    }
                }
            }
        }
    }
     */

    /**
     * Retrieves the ads of this category and all categories that have this category as parentCategory
     *
     * @param name is the name of this category
     * @return a list of ads
     */
    @Override
    public Response getAllAdsInCategoryAndSubCategories(String name) {

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
            return findSubCategories(listIn,listOut, parentName, start + 1);
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
                        icon(category.getIcon()).
                        build();
                categoriesToReturn.add(dto);
            }
        }
        if(categoriesToReturn.size() > 0) {
            // Return all the DTOs
            return new Response(categoriesToReturn,HttpStatus.OK);
        }
        else {
            return new Response("Could not find any parent categories", HttpStatus.NO_CONTENT);
        }
    }

    // Get ad by id
    @Override
    public Response getAdById(long id) {
        Optional<Ad> ad = adRepository.findById(id);
        if(ad.isPresent()) {
            AdDto adDto = modelMapper.map(adRepository.findById(id).get(), AdDto.class);
            return new Response(adDto, HttpStatus.OK);
        }
        else{
            return new Response("Fant ikke annonsen i databasen",HttpStatus.NOT_FOUND);
        }
    }

    // Get all ads for user
    @Override
    public Response getAllAdsByUser(long userId) {
        Set<Ad> adsFound = userRepository.getAdsByUserId(userId);

        if(adsFound != null) {
            return new Response(adsFound.stream()
                    .map(ad -> modelMapper.map(ad, AdDto.class)).
                    collect(Collectors.toList()), HttpStatus.OK);
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
        // Check if size of page is smaller than all ads
        List<Ad> ads = adRepository.findAll();
        if(sizeOfPage <= ads.size()) {
            Pageable pageOf = PageRequest.of(0,sizeOfPage);
            List<AdDto> adDto = adRepository.findAll(pageOf).stream()
                    .map( ad -> modelMapper.map(ad, AdDto.class)).
                    collect(Collectors.toList());
            return new Response(adDto, HttpStatus.OK);
        }
        else {
            List<AdDto> adDtos = adRepository.findAll().stream()
                    .map( ad -> modelMapper.map(ad, AdDto.class)).
                            collect(Collectors.toList());
            return new Response(adDtos, HttpStatus.OK);
        }
    }

    // Get all available ads
    @Override
    public Response getAllAvailableAds() {
        List<AdDto> availableAds = adRepository.getAllAvailableAds().stream()
                .map(ad -> modelMapper.map(ad,AdDto.class)).
                collect(Collectors.toList());

        // If the db contains any available ads
        if(availableAds.size() != 0) {
            return new Response(availableAds, HttpStatus.OK);
        }

        // The db did not contain any available ads
        else {
            return new Response("Fant ingen annonser", HttpStatus.NO_CONTENT);
        }
    }

    // Get all available ads by user id
    @Override
    public Response getAllAvailableAdsByUser(long userId) {
        if(userRepository.existsById(userId)) {
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
        else{
            return new Response("Could not find user with specified id", HttpStatus.NOT_FOUND);
        }
    }

    // Get all ads by postal code
    @Override
    public Response getAllAdsByPostalCode(int postalCode) {
        Set<Ad> availableAds = adRepository.findByPostalCode(postalCode);

        ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

        // Iterate over all ads and create dtos
        for(Ad ad : availableAds) {
            AdDto newAd = modelMapper.map(ad, AdDto.class);
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
     *      - Being given away = false
     *      - Being rented out = true
     */
    // Get all ads by rental type
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

    @Override
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
     * @return response
     */
    @Override
    public Response postNewAd(AdDto adDto) throws IOException, InterruptedException {
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
        setCoordinatesOnAd(newAd);

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
    ;

        // decompressing and converting images in support method
        // convertPictures(ad, adDto);
        return adDto;
    }

    /**
     * support method to decompress pictures
     * @param // ad ad object from database
     * @param // adDto dto object to be returned
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
        // If the reviews-list contains anything
        if(reviews.size() > 0) {
            return new Response(adRepository.getReviewsByUserId(userId).stream().map(review -> modelMapper
                    .map(review, ReviewDto.class)).collect(Collectors.toList()), HttpStatus.OK);
        }
        else {
            return new Response("Fant ingen omtaler på denne brukeren", HttpStatus.NOT_FOUND);
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
            return new Response("Fant ikke annonsen", HttpStatus.NOT_FOUND);
        }
        return new Response("Annonsen er oppdatert", HttpStatus.OK);
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

            // Get all the rentals
            Set<Rental> rentals = ad.get().getRentals();
            if(rentals != null) {
                for(Rental rental : rentals) {
                    rental.setAd(null);
                    rentalRepository.save(rental);
                }
            }
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
            return new Response("Annonsen er slettet", HttpStatus.OK);
        }
        else {
            return new Response("Fant ikke annonsen", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * method to delete a picture on an ad
     * @param ad_id id
     * @param chosenPicture picture_id
     * @return response with status ok or not found
     */
    @Override
    public Response deletePicture(long ad_id, List<MultipartFile> chosenPicture) throws IOException {
        Optional<Ad> ad = adRepository.findById(ad_id);

        // If present
        if(ad.isPresent()) {
            Set<Picture> pictures = ad.get().getPictures();
            if(pictures != null) {
                int i = 0;
                for (Picture picture : pictures) {
                    if(Arrays.equals(picture.getData(), chosenPicture.get(i).getBytes())) {
                        // Remove this picture from ad
                        ad.get().getPictures().remove(picture);

                        // Set the foreign keys of the picture equal to null
                        picture.setAd(null);
                        picture.setUser(null);

                        // Delete the PICTURE
                        pictureRepository.delete(picture);

                        // Update the ad
                        adRepository.save(ad.get());

                        return new Response("Slettet bildet", HttpStatus.OK);
                    }
                    i++;
                }
            }
            // If we get here, pictures are equal to null
            return new Response("Bildet ble ikke funnet i databasen", HttpStatus.NOT_FOUND);
        }
        return new Response("Annonsen med spesifisert ID ikke funnet", HttpStatus.NOT_FOUND);
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

    @Override
    public Response searchThroughAds(String searchword){
        //List to be filled with corresponding ads
        List<Ad> adsContainingSearchWord = new ArrayList<>();

        List<Ad> ads = adRepository.findAll();

        //Checking all titles for searchword
        for(Ad a: ads){
            if(a.getTitle().toLowerCase().contains(searchword.toLowerCase())){
                adsContainingSearchWord.add(a);
            }
        }
        List<Category> categories = categoryRepository.findAll();

        //Adding all ads with the category
        for(Category c: categories){
            if(c.getName().toLowerCase().contains(searchword.toLowerCase())) {
                for (Ad a : c.getAds()) {
                    if(!adsContainingSearchWord.contains(a)) {
                        adsContainingSearchWord.add(a);
                    }
                }
            }
        }

        //Casting objects to Dto and returning
        return new Response(adsContainingSearchWord.stream()
                .map(ad1 -> modelMapper.map(ad1, AdDto.class)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @Override
    public Response sortArrayByPriceAscending(List<AdDto> list){
        list.sort(Comparator.comparing(AdDto::getPrice));
        return new Response(list, HttpStatus.OK);
    }

    @Override
    public Response sortArrayByPriceDescending(List<AdDto> list){
        list.sort(Comparator.comparing(AdDto::getPrice).reversed());
        return new Response(list, HttpStatus.OK);
    }

    @Override
    public Response sortArrayByDistanceAscending(List<AdDto> list){
        list.sort(Comparator.comparing(AdDto::getDistance));
        return new Response(list, HttpStatus.OK);
    }

    @Override
    public Response sortArrayByDistanceDescending(List<AdDto> list){
        list.sort(Comparator.comparing(AdDto::getDistance).reversed());
        return new Response(list, HttpStatus.OK);
    }

    @Override
    public Response getListWithinDistanceIntervall(List<AdDto> list, double limit){
        list.stream().filter(x -> x.getDistance()<limit).collect(Collectors.toList());
        return new Response(list, HttpStatus.OK);
    }

    @Override
    public Response getListOfAdsWithinPriceRange(List<AdDto> list, double upperLimit, double lowerLimit){
        list.stream().filter(x->lowerLimit<x.getPrice() && x.getPrice()<upperLimit).collect(Collectors.toList());
        return new Response(list, HttpStatus.OK);
    }

    private void setCoordinatesOnAd(Ad ad)
            throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        Geocoder geocoder = new Geocoder();

        String response = geocoder.GeocodeSync(ad.getStreetAddress() + ad.getPostalCode() + ad.getCity());
        JsonNode responseJSONnode = objectMapper.readTree(response);
        logger.info("recieved response: " + response);
        JsonNode items = responseJSONnode.get("items");

        for(JsonNode item : items){
            JsonNode address = item.get("address");
            String label = address.get("label").asText();
            JsonNode position = item.get("position");

            String lat = position.get("lat").asText();
            String lng = position.get("lng").asText();
            System.out.println(label + " is located at " + lat + "," + lng + ".");
           if(!lng.equals("") && !lat.equals("")) {
               ad.setLat(Double.parseDouble(lat));
               ad.setLng(Double.parseDouble(lng));
           }
        }
    }

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
    public Response sortArrayOfAdsByDateNewestFirst(List<AdDto> list){
        list.sort(Comparator.comparing(AdDto::getCreated));
        return new Response(list, HttpStatus.OK);
    }

    @Override
    public Response sortArrayOfAdsByDateOldestFirst(List<AdDto> list){
        list.sort(Comparator.comparing(AdDto::getCreated).reversed());
        return new Response(list, HttpStatus.OK);
    }

    @Override
    public Response storeImageForAd(long adId, MultipartFile file) throws IOException {

        return pictureService.savePicture(file, adId, 0);
    }

    //TODO: do we use this?
    public Response getPicture(long pictureId) {
        Optional<Picture> picture = pictureRepository.findById(pictureId);

        if(picture.isPresent()) {
            return new Response(picture.get(), HttpStatus.OK);
        }
        else {
            return new Response("Could not find picture with specified id", HttpStatus.NOT_FOUND);
        }
    }
}

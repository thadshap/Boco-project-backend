package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.dto.CategoryDto;
import com.example.idatt2106_2022_05_backend.dto.PictureReturnDto;
import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.dto.ad.FilterListOfAds;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.util.Geocoder;
import com.example.idatt2106_2022_05_backend.service.calendar.CalendarService;
import com.example.idatt2106_2022_05_backend.util.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class implementation of {@link AdService}
 */
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
    private CalendarService calendarService;

    private ModelMapper modelMapper = new ModelMapper();

    private Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);

    // Get all ads

    /**
     * Method to get all ads.
     *
     * @return all ads.
     * @throws IOException modelmapper.
     */
    @Override
    public Response getAllAds() throws IOException {
        List<Ad> allAds = adRepository.findAll();

        List<AdDto> adsToBeReturned = new ArrayList<>();

        // Iterate over all ads and create DTOs
        for (Ad ad : allAds) {
            AdDto newAd = castObject(ad);
            adsToBeReturned.add(newAd);
        }
        if (adsToBeReturned.size() == 0) {
            return new Response(adsToBeReturned, HttpStatus.NO_CONTENT);
        }
        return new Response(adsToBeReturned, HttpStatus.OK);
    }

    /**
     * Method to get all ads in a category by category name.
     *
     * @param name name of category.
     * @return returns ads if found.
     */
    @Override
    public Response getAllAdsInCategory(String name) {
        Set<Category> categories = categoryRepository.findByName(name);

        List<AdDto> adsToReturn = new ArrayList<>();

        // If category exists
        if (categories != null) {
            // Get all categories
            for (Category category : categories) {
                Set<Ad> adsFound = category.getAds();

                if (adsFound != null) {
                    for (Ad ad : adsFound) {
                        try {
                            AdDto newDto = castObject(ad);
                            newDto.setLat(ad.getLat());
                            newDto.setLng(ad.getLng());
                            adsToReturn.add(newDto);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // Return the ads
            return new Response(adsToReturn, HttpStatus.OK);
        } else {
            return new Response("Fant ikke kategorien", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Method to get all ads in category by category id
     *
     * @param categoryId
     * @return
     */
    @Override
    public Response getAllAdsInCategory(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);

        // List to return
        ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

        // If category exists
        if (category.isPresent()) {
            Set<Ad> adsFound = category.get().getAds();

            for (Ad ad : adsFound) {
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
        } else {
            return new Response("Fant ikke kategorien.", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Method to get all sub categories of a parent category.
     *
     * @param parentName parent category
     * @return returns array of sub-category categoryDtos, or erro message if none found
     */
    @Override
    public Response getAllSubCategories(String parentName) {
        // List that will be returned
        ArrayList<CategoryDto> subCategories = new ArrayList<>();

        // Retrieve all categories from database
        List<Category> categories = categoryRepository.findAll();

        // Iterate over all categories
        for (Category category : categories) {

            // Ensure null-safety by skipping the category if it does not have a parent
            if (category.getParentName() != null) {

                // Using equals w/ignore case() to ensure equality
                if (parentName.equalsIgnoreCase(category.getParentName())) {

                    // Generate a new list that holds only the ids --> avoids recursive stackOverflow
                    ArrayList<Long> ids = new ArrayList<>();

                    // If this category has any ads
                    if (category.getAds().size() > 0) {
                        for (Ad ad : category.getAds()) {
                            ids.add(ad.getId());
                        }
                    }

                    // Create dto
                    CategoryDto dto = CategoryDto.builder().id(category.getId()).name(category.getName())
                            .parentName(parentName).parent(category.isParent()).child(category.isParent()).adIds(ids)
                            .build();

                    // Add to list of sub-categories to return
                    subCategories.add(dto);
                }
            }
        }

        // Return the list if any subcategories were added
        if (subCategories.size() > 0) {
            return new Response(subCategories, HttpStatus.OK);
        }
        // Return NOT_FOUND if there
        else {
            return new Response("No sub categories found with the specified parent-name", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Method to get all categories in a level.
     *
     * @return returns array of categoryDtos in level.
     */
    @Override
    public Response getAllCategoriesWithLevel() {
        List<Category> categories = categoryRepository.findAll();
        ArrayList<CategoryDto> categoriesToReturn = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            categoriesToReturn.add(CategoryDto.builder().level(categories.get(i).getLevel())
                    .icon(categories.get(i).getIcon()).child(categories.get(i).isChild())
                    .parent(categories.get(i).isParent()).parentName(categories.get(i).getParentName())
                    .name(categories.get(i).getName()).id(categories.get(i).getId()).build());
        }
        return new Response(categoriesToReturn, HttpStatus.OK);
    }

    /**
     * Method to get all categories.
     *
     * @return returns list of categoryDtos
     */
    @Override
    public Response getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        // List that will be returned
        ArrayList<CategoryDto> categoriesToReturn = new ArrayList<>();

        // Create all dto
        for (Category category : categories) {

            // Generate a new list that holds only the ids --> avoids recursive stackOverflow
            ArrayList<Long> ids = new ArrayList<>();

            // If this category has any ads
            if (category.getAds().size() > 0) {
                for (Ad ad : category.getAds()) {
                    ids.add(ad.getId());
                }
            }

            // Create dto
            CategoryDto dto = CategoryDto.builder().id(category.getId()).name(category.getName())
                    .parentName(category.getParentName()).parent(category.isParent()).child(category.isChild())
                    .adIds(ids).build();

            // Add to list of sub-categories to return
            categoriesToReturn.add(dto);
        }

        // Return the list if any subcategories were added
        if (categoriesToReturn.size() > 0) {
            return new Response(categoriesToReturn, HttpStatus.OK);
        }
        // Return NOT_FOUND if there
        else {
            return new Response("Fant ikke kategorien", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Get each category and their subCategories. A new list is created for each parent. Each parent's child also is
     * iterated over
     *
     * public void getCategoryHierarchies() { List<Category> allCategories = categoryRepository.findAll();
     * List<Category> mainCategories = new ArrayList<>();
     *
     * // List of parents contains list of children which contains list of children's children
     * List<List<List<Category>>> listsToReturn = new ArrayList<>();
     *
     * int placementOfParent = 0;
     *
     * // Find all the parent categories for(Category category : allCategories) { if(category.isParent()) {
     * mainCategories.add(category); } placementOfParent ++; }
     *
     * // From main categories, find all the children for(Category parentCategory : mainCategories) { // Create a list
     * for each category ArrayList<Category> children = new ArrayList<>(); // Add the list to the list of all lists
     * listsToReturn.add(children);
     *
     * // Iterate over all categories for each parentCategory for (Category aCategory : allCategories) { // Create a
     * list for each sub-category ArrayList<Category> childrenOfChildren = new ArrayList<>();
     *
     * // Add the list to the list of all lists listsToReturn.add(children); // Counter to retrieve the placement in
     * list of lists (due to enhanced for loop) placementOfCurrentList ++; // If the category is not a parent
     * if(!aCategory.isParent()) { // If the parentName of the category is the parentCategory
     * if(aCategory.getParentName().equalsIgnoreCase(parentCategory.getName())) { // Add the category to the parent's
     * list of children (inside the list of lists) listsToReturn.get(placementOfCurrentList - 1).add(aCategory); } } } }
     * }
     */

    /**
     * Retrieves the ads of this category and all categories that have this category as parentCategory
     *
     * @param name is the name of this category
     * @return a list of ads
     */
    @Override
    public Response getAllAdsInCategoryAndSubCategories(String name, UserGeoLocation userGeoLocation) {

        // Retrieve ParentCategory
        Set<Category> categoryFound = categoryRepository.findByName(name);
        Category category = categoryFound.stream().findFirst().get();

        // List of subCategories found using recursive function
        List<Category> subCategories = findSubCategories(category.getId());
        ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

        if (subCategories != null) {
            // Iterate over all sub-categories found
            for (Category category1 : subCategories) {
                // Iterate over all ads in category
                if (category1.getAds() != null) {
                    for (Ad ad : category1.getAds()) {
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
        }

        // Find the parent category
        Set<Category> parentCategories = categoryRepository.findByName(name);

        // There should only be ONE category in the set
        Category parentCategory = parentCategories.stream().findFirst().get();

        // Now, also add the ads connected to only the parent category to the list!
        if (parentCategory.getAds() != null) {
            for (Ad ad : parentCategory.getAds()) {
                AdDto dto = null;
                try {
                    dto = castObject(ad);
                    adsToBeReturned.add(dto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Calculation and setting distance for ads
        for (AdDto a : adsToBeReturned) {
            a.setDistance(
                    calculateDistance(userGeoLocation.getLat(), userGeoLocation.getLng(), a.getLat(), a.getLng()));
        }
        if (adsToBeReturned.size() > 0) {
            // sort so nearest ads comes first
            adsToBeReturned.sort(Comparator.comparing(AdDto::getDistance));

            // Now all ads are returned
            return new Response(adsToBeReturned, HttpStatus.OK);
        } else {
            return new Response(null, HttpStatus.OK);
        }
    }

    /**
     * Method to find all sub-categories of a parent category.
     *
     * @param categoryId id of parent category
     * @return returns list of sub-categories
     */
    @Override
    public List<Category> findSubCategories(long categoryId) {
        Optional<Category> categoryFound = categoryRepository.findById(categoryId);

        List<Category> toReturn = new ArrayList<>();

        if (categoryFound.isPresent()) {

            // Get the category
            Category category = categoryFound.get();

            // Get all categories
            List<Category> allCategories = categoryRepository.findAll();
            if (category.getLevel() == 1) {
                for (Category cat : allCategories) {
                    if (cat.getLevel() == 2) {
                        // If cat's name equals category.getName()
                        if (Objects.equals(cat.getParentName(), category.getName())) {
                            // Add cat to list of categories to return, then iterate over all categories (lvl 3)
                            toReturn.add(cat);
                            for (Category cat2 : allCategories) {
                                if (cat2.getLevel() == 3) {
                                    // If the third lvl category has the 2nd level category as its parent
                                    if (Objects.equals(cat2.getParentName(), cat.getName())) {
                                        toReturn.add(cat2);
                                    }
                                }
                            }
                        }
                    }
                }
                return toReturn;
            }
            if (category.getLevel() == 2) {
                for (Category cat2 : allCategories) {
                    if (cat2.getLevel() == 3) {
                        // If the third lvl category has the 2nd level category as its parent
                        if (Objects.equals(cat2.getParentName(), category.getName())) {
                            toReturn.add(cat2);
                        }
                    }
                }
                return toReturn;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Recursive function that finds all sub-categories belonging to a category (including the sub-categories of
     * sub-categories and so on)
     *
     * @param //listIn
     *            is a list containing all categories in db
     * @param //listOut
     *            is an empty list that is being filled up with sub-categories as the method recursively iterates
     *
     * @return listOut
     *
     *         private List<Category> findSubCategories(ArrayList<Category> listIn, ArrayList<Category> listOut, String
     *         parentName, int start) { // Position in array == start int arrayLength = start;
     *
     *         // Make a counter and if it is not == 1 && base case is not reached when the loop ends, // call on the
     *         function again from parentName == arrayLength.getName int loopCounter = 0;
     *
     *         // Base case: If the position in the array is equal to the size of the array if (arrayLength ==
     *         listIn.size()) { // Return the list that now contains all sub-categories return listOut; } else { //
     *         Iterate through all categories for (int i = start; i < listIn.size(); i++) { Category category =
     *         listIn.get(i);
     *
     *         // If the category is a sub-class if (category.getParentName() != null) {
     *
     *         // If a category has current category as parent category if
     *         (category.getParentName().equalsIgnoreCase(parentName)) {
     *
     *         // Add the category to the list to be returned listOut.add(category);
     *
     *         // This category is now the new parent parentName = category.getName();
     *
     *         // Call on the function recursively from the start for this category findSubCategories(listIn, listOut,
     *         parentName, start); } } } // Increment the list and call on the function recursively return
     *         findSubCategories(listIn, listOut, parentName, start + 1); } }
     */

    /**
     * Method to get all parent categories of a sub-category.
     *
     * @return returns all parent categories.
     */
    @Override
    public Response getAllParentCategories() {
        List<Category> allCategories = categoryRepository.findAll();
        List<CategoryDto> categoriesToReturn = new ArrayList<>();

        for (Category category : allCategories) {
            if (category.isParent()) {
                CategoryDto dto = CategoryDto.builder().child(category.isChild()).parent(category.isParent())
                        .id(category.getId()).name(category.getName()).icon(category.getIcon()).build();
                categoriesToReturn.add(dto);
            }
        }
        if (categoriesToReturn.size() > 0) {
            // Return all the DTOs
            return new Response(categoriesToReturn, HttpStatus.OK);
        } else {
            return new Response("Could not find any parent categories", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Method to get an ad by its id.
     *
     * @param id id of ad to find.
     * @return returns adDto if found
     */
    @Override
    public Response getAdById(long id) {
        Optional<Ad> ad = adRepository.findById(id);
        if (ad.isPresent()) {
            AdDto adDto = modelMapper.map(adRepository.findById(id).get(), AdDto.class);
            return new Response(adDto, HttpStatus.OK);
        } else {
            return new Response("Fant ikke annonsen i databasen", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Method to get all ads of a user.
     *
     * @param userId id of user
     * @return returns response with a list of ads
     */
    @Override
    public Response getAllAdsByUser(long userId) {
        Set<Ad> adsFound = userRepository.getAdsByUserId(userId);

        if (adsFound != null) {
            return new Response(
                    adsFound.stream().map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList()),
                    HttpStatus.OK);
        } else {
            return new Response("Fant ingen annonser p?? brukeren", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Get a page of ads
     *
     * @param sizeOfPage number of the page size
     * @return Response with page in body
     */
    @Override
    public Response getPageOfAds(int sizeOfPage, UserGeoLocation userGeoLocation) {
        // Check if size of page is smaller than all ads
        List<Ad> ads = adRepository.findAll();
        List<AdDto> adDtos = new ArrayList<>();
        if (sizeOfPage <= ads.size()) {
            Pageable pageOf = PageRequest.of(0, sizeOfPage);
            adDtos = adRepository.findAll(pageOf).stream().map(ad -> modelMapper.map(ad, AdDto.class))
                    .collect(Collectors.toList());

        } else {
            adDtos = adRepository.findAll().stream().map(ad -> modelMapper.map(ad, AdDto.class))
                    .collect(Collectors.toList());
        }
        for (AdDto a : adDtos) {
            a.setDistance(
                    calculateDistance(userGeoLocation.getLng(), userGeoLocation.getLat(), a.getLat(), a.getLng()));
        }
        // exceptionhandling
        if (adDtos.size() > 0) {
            adDtos.sort(Comparator.comparing(AdDto::getDistance));
            return new Response(adDtos, HttpStatus.OK);
        }
        return new Response("Fant ingen annonser p?? denne foresp??rselen", HttpStatus.NO_CONTENT);
    }

    /**
     * Method to get all available ads in the repository.
     *
     * @return retunrs response with a list of AdDtos.
     */
    @Override
    public Response getAllAvailableAds() {
        List<AdDto> availableAds = adRepository.getAllAvailableAds().stream()
                .map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList());

        // If the db contains any available ads
        if (availableAds.size() != 0) {
            return new Response(availableAds, HttpStatus.OK);
        }

        // The db did not contain any available ads
        else {
            return new Response("Fant ingen annonser", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Method to get all available ads by user.
     *
     * @param userId id of user
     * @return returns response with list of AdDtos
     */
    @Override
    public Response getAllAvailableAdsByUser(long userId) {
        if (userRepository.existsById(userId)) {
            List<AdDto> availableAds = adRepository.getAvailableAdsByUserId(userId).stream()
                    .map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList());
            // If the db contains any available ads
            if (availableAds.size() != 0) {
                return new Response(availableAds, HttpStatus.OK);
            }
            // The db did not contain any available ads
            else {
                return new Response("Could not find any available ads for that user", HttpStatus.NO_CONTENT);
            }
        } else {
            return new Response("Could not find user with specified id", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Method to get all ads in a postal code.
     *
     * @param postalCode postal code to search.
     * @return returns response with a list of available ads
     */
    @Override
    public Response getAllAdsByPostalCode(int postalCode) {
        Set<Ad> availableAds = adRepository.findByPostalCode(postalCode);

        ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

        // Iterate over all ads and create dtos
        for (Ad ad : availableAds) {
            AdDto newAd = modelMapper.map(ad, AdDto.class);
            adsToBeReturned.add(newAd);
        }

        // If the db contains any available ads
        if (availableAds.size() > 0) {
            return new Response(adsToBeReturned, HttpStatus.OK);
        } else {
            return new Response("Could not find any available ads", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Get all ads with items that are: - Being given away = false - Being rented out = true
     */
    /**
     * Method that gets all ads with items that are:
     * - Being given away = false
     * - Being rented out = true
     *
     * @param rentalType type of rental
     * @return returns response with a list of AdDtos
     */
    @Override
    public Response getAllAdsByRentalType(boolean rentalType) {
        Set<AdDto> ads = adRepository.findByRental(rentalType).stream().map(ad -> modelMapper.map(ad, AdDto.class))
                .collect(Collectors.toSet());

        if (ads.size() != 0) {
            return new Response(ads, HttpStatus.OK);
        } else {
            return new Response("Could not find ads", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Method to get all ads in given city
     *
     * @param city city to find ads in
     * @return returns response with list of AdDtos
     */
    @Override
    public Response getAllAdsInCity(String city) {

        // Retrieve all ads with specified city
        Set<Ad> adsFound = adRepository.findByCity(city);

        // Create list of DTOs to return
        Set<AdDto> adsToReturn = new HashSet<>();

        if (adsFound != null) {
            for (Ad ad : adsFound) {
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
        return new Response("No ads found in specified city", HttpStatus.NO_CONTENT);
    }

    /**
     * Posts new ad
     *
     * @param adDto must contain: - rental (being rented out or given away) - duration (quantity of duration type) -
     *              durationType (type of duration --> see "AdType" enum) - categoryId (only the id of the nearest
     *              category) - price - street_address (of the item) - postal_code (of the item) - name (header of the ad)
     * @param adDto must contain: - rental (being rented out or given away) - duration (quantity of duration type) -
     *              durationType (type of duration --> see "AdType" enum) - categoryId (only the id of the nearest
     *              category) - price - street_address (of the item) - postal_code (of the item) - name (header of the ad)
     *              <p>
     *              can contain (nullable in db): - description - picture (pictures of the item to be rented out) -
     *              rentedOut (true if the item is rented out, which it should be at initialization)
     * @return response
     */
    @Override
    public Response postNewAd(AdDto adDto) throws IOException, InterruptedException {
        Ad newAd = new Ad();

        // Required attributes
        newAd.setRental(adDto.isRental());
        newAd.setRentedOut(false);
        newAd.setDurationType(adDto.getDurationType());
        newAd.setPrice(adDto.getPrice());
        newAd.setStreetAddress(adDto.getStreetAddress());
        newAd.setTitle(adDto.getTitle());
        newAd.setPostalCode(adDto.getPostalCode());
        newAd.setCity(adDto.getCity());
        setCoordinatesOnAd(newAd);

        // If category exists
        Optional<Category> category = categoryRepository.findById(adDto.getCategoryId());
        if (category.isPresent()) {
            newAd.setCategory(category.get());
        }
        // If the category given is null or wrong, the ad cannot be created
        else {
            return new Response("Fant ikke kategorien", HttpStatus.NO_CONTENT);
        }

        Optional<User> user = userRepository.findById(adDto.getUserId());

        if (user.isPresent()) {
            // Set foreign key
            newAd.setUser(user.get());
        } else {
            return new Response("could not find user", HttpStatus.NOT_FOUND);
        }

        // Checking if dto contains any of the nullable attributes
        if (adDto.getDescription() != null) {
            newAd.setDescription(adDto.getDescription());
        }

        // Persisting the entities
        Ad savedAd = adRepository.save(newAd);

        // Set the dates for the ad!
        newAd.setDates(calendarService.addFutureDates(savedAd.getId()));

        user.get().setAd(newAd);
        userRepository.save(user.get());

        category.get().getAds().add(newAd);
        categoryRepository.save(category.get());

        return new Response(newAd.getId(), HttpStatus.CREATED);
    }

    /**
     * method that goes through all ads and returns the with the calculated distance
     *
     * @param userGeoLocation users location
     *
     * @return a list of ads including the distance to the users location
     *
     * @throws IOException if decompression pictures fails
     */
    public Response getAllAdsWithDistance(UserGeoLocation userGeoLocation) throws IOException {
        ArrayList<AdDto> ads = new ArrayList<>();

        for (Ad ad : adRepository.findAll()) {
            // Setting all attributes and decompressing pictures in help method
            AdDto adDto = castObject(ad);
            // Calculate and set distance
            adDto.setDistance(
                    calculateDistance(userGeoLocation.getLat(), userGeoLocation.getLng(), ad.getLat(), ad.getLng()));
            // Adding all ads to list and then response
            ads.add(adDto);
        }
        // In case no ads were found
        if (ads.size() > 0) {
            return new Response(
                    ads.stream().sorted(Comparator.comparing(AdDto::getDistance)).collect(Collectors.toList()),
                    HttpStatus.OK);
        }

        return new Response("Fant ingen ads.", HttpStatus.NO_CONTENT);
    }

    /**
     * support method that creates a dto of ad
     *
     * @param ad ad
     *
     * @return ad dto
     *
     * @throws IOException if decompression of bytes fails
     */
    private AdDto castObject(Ad ad) throws IOException {
        AdDto adDto = modelMapper.map(ad, AdDto.class);

        // decompressing and converting images in support method
        // convertPictures(ad, adDto);
        return adDto;
    }

    /**
     * Method that calculates distance between two geolocations
     *
     * @param lat1  latitude user
     * @param long1 longitude user
     * @param lat2  latitude item
     * @param long2 longitude item
     *
     * @return distance in km
     */
    public double calculateDistance(double lat1, double long1, double lat2, double long2) {
        double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
        return dist / 1000;
    }

    /**
     * Method to get all reviews for an add with owners id.
     *
     * @param userId id of owner
     *
     * @return returns response with reviews
     */
    @Override
    public Response getReviewsByUserId(long userId) {

        Set<Review> reviews = adRepository.getReviewsByUserId(userId);
        // If the reviews-list contains anything
        if (reviews.size() > 0) {
            return new Response(
                    adRepository.getReviewsByUserId(userId).stream()
                            .map(review -> modelMapper.map(review, ReviewDto.class)).collect(Collectors.toList()),
                    HttpStatus.OK);
        } else {
            return new Response("Fant ingen omtaler p?? denne brukeren", HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Method to update an ads information.
     *
     * @param adId        id of ad to update
     * @param adUpdateDto {@link AdUpdateDto} object with information needed to update an ad
     * @return returns response with update status.
     */
    @Override
    public Response updateAd(Long adId, AdUpdateDto adUpdateDto) {

        Optional<Ad> foundAd = adRepository.findById(adId);

        if (foundAd.isPresent()) {

            Ad ad = foundAd.get();

            // Update the ad
            if (adUpdateDto.getTitle() != null) {
                ad.setTitle(adUpdateDto.getTitle());
            }
            if (adUpdateDto.getDescription() != null) {
                ad.setDescription(adUpdateDto.getDescription());
            }
            if (adUpdateDto.getDurationType() != null) {
                ad.setDurationType(adUpdateDto.getDurationType());
            }
            if (adUpdateDto.getPrice() > 0) {
                ad.setPrice(adUpdateDto.getPrice());
            }
            if (adUpdateDto.getStreetAddress() != null) {
                ad.setStreetAddress(adUpdateDto.getStreetAddress());
            }
            if (adUpdateDto.getPostalCode() > 0) {
                ad.setPostalCode(adUpdateDto.getPostalCode());
            }
            if (adUpdateDto.getCity() != null) {
                ad.setCity(adUpdateDto.getCity());
            }

            if (adUpdateDto.getRentedOut() == true) {
                ad.setRentedOut(false);
            }
            if (adUpdateDto.getRentedOut() == false) {
                ad.setRentedOut(true);
            }

            adRepository.save(ad);
        } else {
            return new Response("Fant ikke annonsen", HttpStatus.NOT_FOUND);
        }
        return new Response("Annonsen er oppdatert", HttpStatus.OK);
    }

    /**
     * Method to delete an ad.
     *
     * @param adId
     *              id of ad to be deleted
     *
     * @return returns response with status
     */
    @Override
    public Response deleteAd(long adId) {
        Optional<Ad> ad = adRepository.findById(adId);

        // If the ad exists
        if (ad.isPresent()) {

            // Delete the pictures from the ad
            if (ad.get().getPictures() != null) {
                for (Picture picture : ad.get().getPictures()) {
                    picture.setAd(null);
                    picture.setUser(null);
                    pictureRepository.save(picture);
                    ad.get().getUser().setPicture(null);
                    userRepository.save(ad.get().getUser());
                }
            }

            for (Picture picture : pictureRepository.findAll()) {
                if (picture.getAd() != null) {
                    if (Objects.equals(picture.getAd().getId(), ad.get().getId())) {
                        picture.setAd(null);
                        pictureRepository.save(picture);
                        // Delete the ad's pictures
                        ad.get().setPictures(null);
                        pictureRepository.delete(picture);
                    }
                }
            }

            // Delete the ad's pictures
            ad.get().setPictures(null);

            // Get all the rentals
            Set<Rental> rentals = ad.get().getRentals();
            if (rentals != null) {
                for (Rental rental : rentals) {
                    rental.setAd(null);
                    rental.setActive(false);
                    rentalRepository.save(rental);
                }
            }

            // Delete its rentals
            ad.get().setRentals(null);

            Set<Review> reviews = ad.get().getReviews();
            if (reviews != null) {
                for (Review review : reviews) {
                    review.setAd(null);
                    reviewRepository.save(review);
                }
            }

            // Delete the reviews todo save these somewhere else during next iteration!
            ad.get().setReviews(null);

            /**
             * // Delete the ad from the dates for (CalendarDate date : ad.get().getDates()) {
             * date.getAds().remove(ad.get()); }
             */

            // Delete the dates from the ad
            ad.get().setDates(null);

            // Delete the ad from its category
            ad.get().getCategory().getAds().remove(ad.get());

            // Delete the ad from its user
            // ad.get().getUser().getAds().remove(ad.get());
            // ad.get().setUser(null);
            ad.get().getUser().getAds().remove(ad.get());

            adRepository.save(ad.get());

            // Delete the ad
            adRepository.delete(ad.get());
            adRepository.deleteById(adId);

            // HttpResponse = OK
            return new Response("Annonsen er slettet", HttpStatus.OK);
        } else {
            return new Response("Fant ikke annonsen", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * method to delete a picture on an ad
     *
     * @param ad_id
     *              id
     * @param chosenPicture
     *              picture_id
     *
     * @return response with status ok or not found
     */
    @Override
    public Response deletePicture(long ad_id, List<MultipartFile> chosenPicture) throws IOException {
        Optional<Ad> ad = adRepository.findById(ad_id);

        // If present
        if (ad.isPresent()) {
            Set<Picture> pictures = ad.get().getPictures();
            if (pictures != null) {
                int i = 0;
                for (Picture picture : pictures) {
                    for (MultipartFile inputFile : chosenPicture) {
                        if (Arrays.equals(picture.getBase64().getBytes(), inputFile.getBytes())) {
                            // Remove this picture from ad
                            ad.get().getPictures().remove(picture);

                            // Set the foreign keys of the picture equal to null
                            picture.setAd(null);
                            picture.setUser(null);

                            // Update the ad
                            adRepository.save(ad.get());

                            // Delete the PICTURE
                            pictureRepository.delete(picture);
                            i++;
                        }
                    }
                }
                return new Response("Slettet " + i + " bilder.", HttpStatus.OK);
            }
            // If we get here, pictures are equal to null
            return new Response("Bildet ble ikke funnet i databasen", HttpStatus.NO_CONTENT);
        }
        return new Response("Annonsen med spesifisert ID ikke funnet", HttpStatus.NO_CONTENT);
    }

    /**
     * Method to search through ads with a searchword.
     *
     * @param searchword
     *              word to search for
     * @param userGeoLocation
     *              {@link UserGeoLocation} object with geolocation information
     *
     * @return returns list of ads matching the search
     */
    @Override
    public Response searchThroughAds(String searchword, UserGeoLocation userGeoLocation) {
        // List to be filled with corresponding ads
        List<Ad> adsContainingSearchWord = new ArrayList<>();

        List<Ad> ads = adRepository.findAll();

        // Checking all titles for searchword
        for (Ad a : ads) {
            if (a.getTitle().toLowerCase().contains(searchword.toLowerCase())) {
                adsContainingSearchWord.add(a);
            }
        }
        List<Category> categories = categoryRepository.findAll();

        // Adding all ads with the category
        for (Category c : categories) {
            if (c.getName().toLowerCase().contains(searchword.toLowerCase())) {
                for (Ad a : c.getAds()) {
                    if (!adsContainingSearchWord.contains(a)) {
                        adsContainingSearchWord.add(a);
                    }
                }
            }
        }
        if (adsContainingSearchWord.size() > 0) {
            List<AdDto> list = adsContainingSearchWord.stream().map(ad1 -> modelMapper.map(ad1, AdDto.class))
                    .collect(Collectors.toList());
            for (AdDto a : list) {
                a.setDistance((calculateDistance(userGeoLocation.getLat(), userGeoLocation.getLng(), a.getLat(),
                        a.getLng())));
            }
            // Casting objects to Dto and returning
            return new Response(list, HttpStatus.OK);
        }
        return new Response("Fant ingen annonser med dette s??keordet.", HttpStatus.NO_CONTENT);
    }

    /**
     * Method to set cordinates on ad.
     *
     * @param ad
     *              ad to change cordinates of
     *
     * @throws IOException
     *
     * @throws InterruptedException
     */
    private void setCoordinatesOnAd(Ad ad) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        Geocoder geocoder = new Geocoder();

        String response = geocoder.GeocodeSync(ad.getStreetAddress() + ad.getPostalCode() + ad.getCity());
        JsonNode responseJSONnode = objectMapper.readTree(response);
        logger.info("recieved response: " + response);
        JsonNode items = responseJSONnode.get("items");

        for (JsonNode item : items) {
            JsonNode address = item.get("address");
            String label = address.get("label").asText();
            JsonNode position = item.get("position");

            String lat = position.get("lat").asText();
            String lng = position.get("lng").asText();
            // logger.debug(label + " is located at " + lat + "," + lng + ".");
            if (!lng.equals("") && !lat.equals("")) {
                ad.setLat(Double.parseDouble(lat));
                ad.setLng(Double.parseDouble(lng));
            }
        }
    }

    /**
     * Method to get all pictures for an ad.
     *
     * @param adId
     *              id of ad to get pictures for
     *
     * @return returns list of PictureReturnDtos of ad pictures
     */
    @Override
    public List<PictureReturnDto> getAllPicturesForAd(long adId) {
        Ad ad = adRepository.getById(adId);
        List<Picture> pictures = pictureRepository.findByAd(ad);
        if (pictures.isEmpty()) {
            return null;
        }
        List<PictureReturnDto> returnDto = new ArrayList<>();
        for (int i = 0; i < pictures.size(); i++) {
            returnDto.add(PictureReturnDto.builder()
                    // .base64(Base64.getEncoder().encodeToString(pictures.get(i).getData()))
                    .base64(pictures.get(i).getBase64()).type(pictures.get(i).getType()).build());
            returnDto.get(i).setId(adId);
        }
        return returnDto;
    }

    /**
     * Method to get first picture for an ad
     *
     * @param adId
     *              id of ad
     *
     * @return returns response with pictures changed
     */
    @Override
    public Response getFirstPictureForAd(long adId) {
        Ad ad = adRepository.getById(adId);
        List<Picture> pictures = pictureRepository.findByAd(ad);
        if (pictures.isEmpty()) {
            return new Response("Annonse med tittel \"" + ad.getTitle() + "\" har ingen bilder", HttpStatus.NOT_FOUND);
        }
        List<PictureReturnDto> returnDto = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            returnDto.add(PictureReturnDto.builder()
                    // .base64(Base64.getEncoder().encodeToString(pictures.get(i).getData())
                    .base64(pictures.get(i).getBase64()).type(pictures.get(i).getType()).build());
            returnDto.get(i).setId(adId);
        }
        return new Response(returnDto.get(0), HttpStatus.OK);
    }

    /**
     * Method to store images for ad
     *
     * @param adId
     *              id of ad to store images for
     * @param files
     *              picture multipart file
     *
     * @return returns response of success status
     *
     * @throws IOException
     */
    @Override
    public Response storeImageForAd(long adId, List<MultipartFile> files) throws IOException {
        Optional<Ad> adOptional = adRepository.findById(adId);
        if (adOptional.isEmpty()) {
            return new Response("Could not find ad", HttpStatus.NOT_FOUND);
        }
        Ad ad = adOptional.get();

        ad.setPictures(new HashSet<>());
        for (int i = 0; i < files.size(); i++) {
            Picture picture = Picture.builder().filename(files.get(i).getName()).type(files.get(i).getContentType())
                    // .data(files.get(i).getBytes())
                    .base64(Base64.getEncoder().encodeToString(files.get(i).getBytes())).build();
            ad.getPictures().add(picture);
            picture.setAd(ad);
            pictureRepository.save(picture);
        }
        adRepository.save(ad);
        return new Response("Bildet er lagret", HttpStatus.OK);
    }

    /**
     * Method to get ads with categories and filter,
     * allows sorting ads by category and a filter
     *
     * @param filterListOfAds
     *              {@link FilterListOfAds} object to store filter information
     *
     * @return returns response with all ads matching category and filter
     */
    @Override
    public Response getAdsWithCategoryAndFilter(FilterListOfAds filterListOfAds) {
        UserGeoLocation userGeoLocation = new UserGeoLocation(filterListOfAds.getLat(), filterListOfAds.getLng());

        // If there is a category we sort for it
        if (filterListOfAds.getCategory() != null) {
            List<AdDto> list = (List<AdDto>) getAllAdsInCategoryAndSubCategories(filterListOfAds.getCategory(),
                    userGeoLocation).getBody();
            filterListOfAds.setList(list);
            return new Response(getAllAdsWithFilter(filterListOfAds), HttpStatus.OK);
        }
        // If there is no category we do not sort for it
        else {
            return new Response(getAllAdsWithFilter(filterListOfAds), HttpStatus.OK);
        }
        /**
         * List<AdDto> list = (List<AdDto>) getAllAdsInCategoryAndSubCategories(filterListOfAds.getCategory(),
         * userGeoLocation).getBody(); filterListOfAds.setList(list); // TODO ELSE() retrieve all ads and filter them!
         * return new Response(getAllAdsWithFilter(filterListOfAds), HttpStatus.OK);
         */
    }

    /**
     * Method to get all ads matching given filter
     *
     * @param filterListOfAds
     *              {@link FilterListOfAds} object to store filter information
     *
     * @return returns response with a list of matching ads
     */
    @Override
    public Response getAllAdsWithFilter(FilterListOfAds filterListOfAds) {
        List<Ad> ads = new ArrayList<>();
        if (filterListOfAds.getList() != null) {
            for (AdDto a : filterListOfAds.getList()) {
                ads.add(adRepository.findById(a.getAdId()).get());
            }
        } else {
            ads = adRepository.findAll();
        }
        List<AdDto> list = new ArrayList<>();
        if (filterListOfAds.getFilterType().toLowerCase().equals("distance")) {
            list = ads.stream().map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList());
        }

        if (filterListOfAds.getFilterType().toLowerCase().equals("price")) {
            logger.debug("Got to service with limits: " + String.valueOf(filterListOfAds.getUpperLimit())
                    + String.valueOf(filterListOfAds.getLowerLimit()));

            for (Ad a : ads) {
                if (a.getPrice() < filterListOfAds.getUpperLimit() && a.getPrice() > filterListOfAds.getLowerLimit()) {

                    logger.info("adding ad with price: " + a.getPrice());
                    list.add(modelMapper.map(a, AdDto.class));
                }
            }
        }
        // Returning array with nearest location
        if (filterListOfAds.getLat() != 0 && filterListOfAds.getLng() != 0) {
            for (AdDto a : list) {
                a.setDistance(
                        calculateDistance(filterListOfAds.getLat(), filterListOfAds.getLng(), a.getLat(), a.getLng()));
            }
            // setting them in the right order
            if (filterListOfAds.isLowestValueFirst()) {
                list.sort(Comparator.comparing(AdDto::getDistance));
            } else {
                list.sort(Comparator.comparing(AdDto::getDistance).reversed());
            }
            // excluding those that are outside the limit of distance
            if (filterListOfAds.getUpperLimit() != 0
                    && filterListOfAds.getFilterType().toLowerCase().equals("distance")) {
                list.removeIf(a -> a.getDistance() > filterListOfAds.getUpperLimit());
            }
            return new Response(list, HttpStatus.OK);
        }

        return new Response("Noe gikk galt i filtreringen.", HttpStatus.NO_CONTENT);
    }
}

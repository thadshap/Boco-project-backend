package com.example.idatt2106_2022_05_backend.service.review;

import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.ReviewRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for CRUD on Review
 */
@Service
public class ReviewServiceImpl implements ReviewService{

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    AdRepository adRepository;

    @Autowired
    UserRepository userRepository;

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * Method validate creation of a new review
     * @param newReviewDto
     */
    private void validate(ReviewDto newReviewDto){
        if(newReviewDto.getDescription().length()>200){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description exceeds maximum lenght");

        }if(0>newReviewDto.getRating() && newReviewDto.getRating()>10){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating outside of scale");
        }
    }



    /**
     * Method to validate that a user only posts once per ad
     * @param ad_id ad
     * @param newPostingUser user
     */
    private void validateUser(long ad_id, User newPostingUser){
        Ad ad = adRepository.getById(ad_id);
        List<Review> allreviews = reviewRepository.getAllByAd(ad);
        for(Review r: allreviews){
            if(r.getUser().getId() == newPostingUser.getId()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user can only post once per ad");
            }
        }
    }

    /**
     * Method to create and save a new review
     * @param newReviewDto dto
     * @return Review
     */
    public Response createNewReview(ReviewDto newReviewDto){
        Review review = new Review();
        validate(newReviewDto);

        review.setDescription(newReviewDto.getDescription());
        review.setRating(newReviewDto.getRating());

        //checking that the same user does not post twice per ad
        User user = userRepository.getById(newReviewDto.getUser_id());
        validateUser(newReviewDto.getAd_id(), user);
        review.setUser(user);

        //Setting ad
        review.setAd(adRepository.getById(newReviewDto.getAd_id()));

        reviewRepository.save(review);

        // Increment the number of reviews for the user
        user.setNumberOfReviews(user.getNumberOfReviews() + 1); // todo if getNumberOfReviews == null implement check

        // Add the rating to the total rating of the user
        user.setRating(user.getRating() + newReviewDto.getRating());

        return new Response("Review successfully added.", HttpStatus.CREATED);
    }




    /**
     * Method retrieves all reviews on an ad
     * @param ad_id ad
     * @return list of reviews
     */
    @Override
    public Response getReviewsByAdId(long ad_id){
        Ad ad = adRepository.getById(ad_id);
        List<ReviewDto> reviews = reviewRepository.getAllByAd(ad).stream()
                .map(review -> modelMapper.map(review, ReviewDto.class)).collect(Collectors.toList());
        //Returns reviews if found
        if(reviews!=null) {
            return new Response(reviews, HttpStatus.OK);
        }
        return new Response(null, HttpStatus.NOT_FOUND);
    }

    /**
     * method to delete a review
     *
     * @param ad_id id of ad to be deleted
     * @param user_id user who wrote the review
     *
     * @return response
     */
    @Override
    public Response deleteReview(long ad_id, long user_id){
        Optional<Review> review = reviewRepository.getByAdAndUser(adRepository.getById(ad_id),
                             userRepository.getById(user_id));
        if(review.isPresent()){

            // Remove the rating from the original ad and user
            Optional<User> userFound = userRepository.findById(review.get().getUser().getId());
            Optional<Ad> adFound = adRepository.findById(ad_id);
            if(userFound.isPresent() && adFound.isPresent()) {

                // If the user who wrote the review is the user trying to delete it
                if(userFound.get().getId().equals(user_id)) {

                    // Remove the review from the user that created it
                    userFound.get().getReviews().remove(review.get());
                    adFound.get().getReviews().remove(review.get());

                    // Find the user that owns the ad
                    User adOwner = adFound.get().getUser();

                    // Remove the rating from that users total rating
                    adOwner.getReviews().remove(review.get());
                    adOwner.setRating(adOwner.getRating() - review.get().getRating());

                    // Decrement that users total number of ratings
                    adOwner.setNumberOfReviews(adOwner.getNumberOfReviews() - 1);

                    // Persist the user that owns the ad
                    userRepository.save(adOwner);

                    // Persist the user that deleted the ad and the ad
                    userRepository.save(userFound.get());
                    adRepository.save(adFound.get());

                    // Remove the foreign keys from the review
                    review.get().setUser(null);
                    review.get().setAd(null);

                    // Delete the review
                    reviewRepository.delete(review.get());
                }
            }
            return new Response("Review was successfully deleted", HttpStatus.OK);
        }
        else {
            return new Response("There was no review with the specified ad id and user id",
                          HttpStatus.NOT_FOUND);
        }
    }
}

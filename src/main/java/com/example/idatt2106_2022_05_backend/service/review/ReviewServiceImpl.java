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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
     * @param newReviewDto reviewDto
     */
    private void validate(ReviewDto newReviewDto){
        if(newReviewDto.getDescription().length()>200){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Beskrivelsen kan ikke være mere enn 200 tegn");

        }if(0>newReviewDto.getRating() && newReviewDto.getRating()>10){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Skalaen for rangering er 1-10");
        }
    }


    /**
     * Method to validate that a user only posts once per ad
     * @param ad ad
     * @param newPostingUser user
     */
    private boolean validateUser(Ad ad, User newPostingUser){
        List<Review> allreviews = reviewRepository.getAllByAd(ad);
        for(Review r: allreviews){
            if(r.getUser().getId() == newPostingUser.getId()){
                return false;
            }
        }
        return true;
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
        User user = getUser(newReviewDto.getUserId());
        Ad ad = getAd(newReviewDto.getAdId());

        if(!validateUser(ad, user)){
            return new Response("en bruker kan kun post 1 omtale per annonse", HttpStatus.BAD_REQUEST);
        }
        review.setUser(user);
        //Setting ad
        review.setAd(ad);

        reviewRepository.save(review);
        return new Response("Omtalen ble lagret", HttpStatus.OK);
    }




    /**
     * Method retrieves all reviews on an ad
     * @param ad_id ad
     * @return list of reviews
     */
    @Override
    public Response getReviewsByAdId(long ad_id){
        Ad ad = getAd(ad_id);
        Set<ReviewDto> reviews = reviewRepository.getAllByAd(ad).stream()
                .map(review -> modelMapper.map(review, ReviewDto.class)).collect(Collectors.toSet());
        //Returns reviews if found
        if(reviews.size()!=0) {
            return new Response(reviews, HttpStatus.OK);
        }
        return new Response("fant ingen omtaler på denne annonsen", HttpStatus.NOT_FOUND);
    }

    /**
     * Method to delete a review.
     * @param ad_id id of ad to be deleted.
     * @param user_id user who wrote the review.
     * @return response.
     */
    @Override
    public Response deleteReview(long ad_id, long user_id){
        Optional<Review> review = reviewRepository.getByAdAndUser(adRepository.getById(ad_id), userRepository.getById(user_id));
        if(review.isPresent()){
            reviewRepository.delete(review.get());
            return new Response("Omtalen ble slettet", HttpStatus.OK);
        }
        return new Response("fant ikke omtalen", HttpStatus.NOT_FOUND);
    }

    //Private support methods:

    /**
     * Helper method to retrieve user by id.
     * @param id id of user.
     * @return user found.
     */
    private User getUser(long id){
        return userRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Fant ikke brukeren"));
    }

    /**
     * Helper method to retrieve Ad by id.
     * @param id id of ad
     * @return ad found.
     */
    private Ad getAd(long id){
        return adRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "fant ikke annonsen"));
    }
}

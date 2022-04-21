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

@Service
public class ReviewServiceImpl implements ReviewService{
    /*
    - get reviews by ad_id
    - get reviews by user_id
     */

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
        return new Response(null, HttpStatus.OK);
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

    @Override
    public Response deleteReview(long ad_id, long user_id){
        Optional<Review> review = reviewRepository.getByAdAndUser(adRepository.getById(ad_id), userRepository.getById(user_id));
        if(review.get()==null){
            return new Response(null, HttpStatus.NOT_FOUND);
        }
        reviewRepository.delete(review.get());
        return new Response("Review was successfully deleted", HttpStatus.OK);
    }
}

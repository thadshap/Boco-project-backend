package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.dto.Review.NewReviewDto;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewService {
    /*
     - create new review
    - get reviews by ad_id
    - get reviews by user_id
     */

    @Autowired
    private ReviewRepository reviewRepository;

    private void validate(NewReviewDto newReviewDto){
        if(newReviewDto.getDescription().length()>200){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description exceeds maximum lenght");

        }if(0>newReviewDto.getRating() && newReviewDto.getRating()>10){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating outside of scale");
        }
    }

    private void createNewReview(NewReviewDto newReviewDto){
        Review review = new Review();
        validate(newReviewDto);

        review.setDescription(newReviewDto.getDescription());
        review.setRating(newReviewDto.getRating());

        //TODO: set ude
    }
}

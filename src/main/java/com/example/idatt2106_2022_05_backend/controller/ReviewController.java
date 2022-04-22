package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.service.review.ReviewService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:8081")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    /**
     * gets all reviews on an ad
     * @param ad_id id
     * @return response
     */
    @GetMapping("/reviews/ad")
    public Response getReviewsByAdId(@RequestBody long ad_id){
        return reviewService.getReviewsByAdId(ad_id);
    }

    /**
     * endpoint for creating and saving a review
     * @param newReview review
     * @return response
     */
    @PostMapping("/new/review")
    public Response createNewReview(@RequestBody ReviewDto newReview){
        return reviewService.createNewReview(newReview);
    }

    @DeleteMapping("/delete/review")
    public Response deleteReview(@RequestBody ReviewDto reviewDto){
        return reviewService.deleteReview(reviewDto.getAd_id(), reviewDto.getUser_id());
    }


}

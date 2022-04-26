package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.service.review.ReviewService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@Api(tags = "Controller class to handle user")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @GetMapping("/reviews/{adId}")
    @ApiOperation(value = "Endpoint to get reviews by ad id", response = Response.class)
    public Response getReviewsByAdId(@PathVariable long adId){
        log.debug("[X] Call to get all reviews of ad by id = {}", adId);
        return reviewService.getReviewsByAdId(adId);
    }

    @PostMapping("/new/review")
    @ApiOperation(value = "Endpoint to create a new review", response = Response.class)
    public Response createNewReview(@RequestBody ReviewDto newReview){
        log.debug("[X] Call to create new review for ad with id = {}", newReview.getAdId());
        return reviewService.createNewReview(newReview);
    }

    @DeleteMapping("/delete/review")
    @ApiOperation(value = "Endpoint to delete a review", response = Response.class)
    public Response deleteReview(@RequestBody ReviewDto reviewDto){
        log.debug("[X] Call to delete review of ad with id = {}", reviewDto.getAdId());
        return reviewService.deleteReview(reviewDto.getAdId(), reviewDto.getUserId());
    }
}

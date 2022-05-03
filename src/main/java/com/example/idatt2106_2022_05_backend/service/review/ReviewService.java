package com.example.idatt2106_2022_05_backend.service.review;

import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

/**
 * Interface for ReviewServiceImpl
 */
@Service
public interface ReviewService {

    // creating a new review
    Response createNewReview(ReviewDto newReviewDto);

    // Getting all reviews on an ad
    Response getReviewsByAdId(long ad_id);

    // deleting review
    Response deleteReview(long ad_id, long user_id);
}

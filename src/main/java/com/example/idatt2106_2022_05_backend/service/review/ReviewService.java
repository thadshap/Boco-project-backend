package com.example.idatt2106_2022_05_backend.service.review;

import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReviewService {


    public Response createNewReview(ReviewDto newReviewDto);

    public Response getReviewsByAdId(long ad_id);

    public Response deleteReview(long ad_id, long user_id);
}

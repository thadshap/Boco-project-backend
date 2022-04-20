package com.example.idatt2106_2022_05_backend.dto.Review;

import lombok.Data;

@Data
public class NewReviewDto {
    private int rating;
    private String description;
    private long user_id;
    private long ad_id;
}

package com.example.idatt2106_2022_05_backend.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private int rating;
    private String description;
    private long user_id;
    private long ad_id;
}

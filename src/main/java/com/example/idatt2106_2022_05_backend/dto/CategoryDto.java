package com.example.idatt2106_2022_05_backend.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class CategoryDto {
    private long id;
    private String name;
    private boolean parent;
    private String parentName;
    private List<Long> adIds;
}

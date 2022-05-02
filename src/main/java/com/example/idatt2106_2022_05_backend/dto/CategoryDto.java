package com.example.idatt2106_2022_05_backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "data transfer object to create and return categories")
public class CategoryDto {

    @ApiModelProperty(notes = "id of the category")
    private long id;

    @ApiModelProperty(notes = "name of the current category")
    private String name;

    @ApiModelProperty(notes = "true if this is a parent-category; false if this is a sub-category")
    private boolean parent;

    @ApiModelProperty(notes = "name of the parent-category if this category has one")
    private String parentName;

    @ApiModelProperty(notes = "icon of the category")
    private String icon;

    @ApiModelProperty(notes = "list containing the ids of the ads that the original Category entity contains")
    private List<Long> adIds;
}

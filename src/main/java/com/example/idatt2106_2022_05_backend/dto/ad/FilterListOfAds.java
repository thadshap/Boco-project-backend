package com.example.idatt2106_2022_05_backend.dto.ad;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@ApiModel(description = "Data transfer object list of ads, used to filter ads")

public class FilterListOfAds {

    @ApiModelProperty(notes = "list of ads to be filtered")
    private List<AdDto> list;

    @ApiModelProperty(notes = "filter to sort after: distance, price")
    private String filterType;

    @ApiModelProperty(notes = "upper limit of price sorting")
    private double upperLimit;

    @ApiModelProperty(notes = "lower limit of price sorting")
    private double lowerLimit;

    @ApiModelProperty(notes = "if user has sorted by a category")
    private String category;

    @ApiModelProperty(notes = "lowest value")
    private boolean lowestValueFirst;

    @ApiModelProperty(notes = "latitude of user, for sorting by distance")
    private double lat;

    @ApiModelProperty(notes = "longitude of user, when sorting by distance")
    private double lng;
}

package com.example.idatt2106_2022_05_backend.dto.rental;

import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Data transfer object for returning list of rentals to user")
public class RentalListDto {

    @ApiModelProperty(notes = "list of rentalDto for user")
    private List<RentalDto> rentals;
}

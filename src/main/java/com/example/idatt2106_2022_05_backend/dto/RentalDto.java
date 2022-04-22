package com.example.idatt2106_2022_05_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RentalDto {

    private LocalDate dateOfRental;

    private LocalDate rentFrom;

    private LocalDate rentTo;

    private LocalDate deadline;

    private Long owner;

    private Long borrower;

    private Long ad;
}

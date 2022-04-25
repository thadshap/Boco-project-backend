package com.example.idatt2106_2022_05_backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class Rental {

    @Id
    @SequenceGenerator(name = "rental_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(generator = "rental_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "rental_id")
    private Long id;

    @NotBlank
    @NotNull
    private LocalDate dateOfRental;

    @NotBlank
    @NotNull
    private LocalDate rentFrom;

    @NotBlank
    @NotNull
    private LocalDate rentTo;

    @NotBlank
    @NotNull
    private LocalDate deadline;

    private boolean active;

    @ManyToOne
    @JoinColumn(referencedColumnName = "userId", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(referencedColumnName = "userId", nullable = false)
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "adId", nullable = false)
    private Ad ad;
}

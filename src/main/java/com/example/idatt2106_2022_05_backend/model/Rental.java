package com.example.idatt2106_2022_05_backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SuperBuilder
@Transactional
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

    private double rating;

    private int price;

    @ManyToOne
    @JoinColumn(referencedColumnName = "user_id")
    private User owner;

    @ManyToOne
    @JoinColumn(referencedColumnName = "user_id")
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @PreRemove
    private void removeRelationships(){
        if(ad != null){
            setAd(null);
        }
        if(borrower != null){
            setBorrower(null);
        }
        if (owner != null) {
            setOwner(null);
        }
    }
}

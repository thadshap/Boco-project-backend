package com.example.idatt2106_2022_05_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name="RATING")
    private int rating;

    @Column(name="DESCRIPTION")
    private String description;

    @ManyToOne
    private User user;

    //TODO: add dependency to Ad

}

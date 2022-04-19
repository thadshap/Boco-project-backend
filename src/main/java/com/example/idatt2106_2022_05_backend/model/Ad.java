package com.example.idatt2106_2022_05_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
/**
 * This table has a many-to-many relationship with teachers
 * This table has a many-to-many relationship with students
 * This table has a many-to-one relationship with administrator (teacher)
 * This table has a one-to-many relationship with queues
 */
public class Ad {

    //address
    //postal_code
    //picture
    //name
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ad_id", nullable = false)
    private Long adId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    // If true --> for rent; if false --> item is being given away
    @Column(name = "rental", nullable = false)
    private boolean rental;

    // True if the item is rented out
    @Column(name = "rented_out", nullable = false)
    private boolean rentedOut;

    // Is nullable
    @Column(name = "price")
    private int price;

    // Is nullable
    @Column(name = "street_address")
    private int streetAddress;
    // Is nullable
    @Column(name = "postal_code")
    private int postalCode;

    // Is nullable
    // todo fk where relationship is zero-to-many
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "ad")
    private Set<Picture> pictures = new HashSet<>();

    // todo one-to-one
    @OneToOne(mappedBy = "ad", cascade = CascadeType.REMOVE)
    private Category category;
}

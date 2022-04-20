package com.example.idatt2106_2022_05_backend.model;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ads")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ad_id", nullable = false)
    private Long adId;

    @Column(name = "title", nullable = false)
    private String title;

    // Is nullable
    @Column(name = "description")
    private String description;

    // If true --> for rent; if false --> item is being given away
    @Column(name = "rental", nullable = false)
    private boolean rental;

    @Column(name = "duration", nullable = false)
    private int duration;

    @Column(name = "duration_type", nullable = false)
    private AdType durationType;

    // True if the item is rented out
    @Column(name = "rented_out", nullable = false)
    private boolean rentedOut;

    // Is nullable
    @Column(name = "price")
    private int price;

    // Is nullable
    @Column(name = "street_address")
    private String streetAddress;
    // Is nullable
    @Column(name = "postal_code")
    private int postalCode;

    // Is nullable
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "ad")
    private Set<Picture> pictures;

    @OneToOne(mappedBy = "ad", cascade = CascadeType.REMOVE)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

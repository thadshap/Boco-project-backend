package com.example.idatt2106_2022_05_backend.model;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Entity
@ToString
@RequiredArgsConstructor
@Builder
@Table(name = "ads")
public class Ad {

    @Id
    @SequenceGenerator(name = "ad_sequence", sequenceName = "ad_sequence", allocationSize = 1)
    @GeneratedValue(generator = "ad_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ad_id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    // Is nullable
    @Column(name = "description")
    private String description;

    // If true --> for rent; if false --> item is being given away
    @Column(name = "rental", nullable = false)
    private boolean rental;

    @Column(name = "duration_type", nullable = false)
    private AdType durationType;

    // True if the item is rented out
    @Column(name = "rented_out", nullable = false)
    private boolean rentedOut;

    // Is nullable
    @Column(name = "price", nullable = false)
    private int price;

    // Is nullable
    @Column(name = "street_address")
    private String streetAddress;

    // Is nullable
    @Column(name = "postal_code", nullable = false)
    private int postalCode;

    @NotNull
    private String city;

    // Coordinates latitude
    @Column(name = "lat")
    private double lat;

    // Coordinates longitude
    @Column(name = "lng")
    private double lng;

    // Created timestamp --> for use in calculating ad-expiration
    @CreationTimestamp
    @Column(name = "created")
    private LocalDate created;

    // Replacing picture table (photos == fileName)
    @Column(length = 64)
    private String photos;

    // Is nullable
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE }, mappedBy = "ad")
    @ToString.Exclude
    private Set<Picture> pictures = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ad", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private Set<Rental> rentals = new HashSet<>();

    // one-to-many connection with review.
    // When an ad is removed, its corresponding reviews are also removed.
    // When ad is persisted, the reviews are also updated
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ad", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private Set<Review> reviews = new HashSet<>();

    // Many to many connection to CalendarDate modelled by the "calendar" table (not modelled)
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE })
    @JoinTable(name = "calendar", joinColumns = { @JoinColumn(name = "ad_id") }, inverseJoinColumns = {
            @JoinColumn(name = "date_id") })
    private Set<CalendarDate> dates = new HashSet<>();

    // Add a new picture
    public void addPicture(Picture picture) {
        pictures.add(picture);
    }

    // Adding a getter to retrieve the image path of the photos of this specific ad
    @Transient
    public String getPhotosImagePath() {
        if (photos == null || id == null)
            return null;

        return "/ad-photos/" + id + "/" + photos;
    }

    @PreRemove
    private void removeRelationships() {
        if (dates != null) {
            setDates(null);
        }
        if (user != null) {
            setUser(null);
        }
        if (category != null) {
            setCategory(null);
        }
    }

    /**
     * // @PreRemove private void removeRelationships() { if (pictures != null) { setPictures(null); } if (category !=
     * null) { setCategory(null); } if (rentals != null) { setRentals(null); } if (reviews != null) { setReviews(null);
     * } if (dates != null) { setDates(null); } if (user != null) { setUser(null); } }
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Ad ad = (Ad) o;
        return id != null && Objects.equals(id, ad.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

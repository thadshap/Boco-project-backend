package com.example.idatt2106_2022_05_backend.model;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
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
    @Column(name = "adId")
    private Long id;

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

    // Coordinates latitude
    @Column(name="LAT")
    private double lat;

    // Coordinates longitude
    @Column(name="LNG")
    private double lng;

    // Created timestamp --> for use in calculating ad-expiration
    @CreationTimestamp
    @Column(name = "created")
    private LocalDate created;

    // Is nullable
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "ad")
    @ToString.Exclude
    private Set<Picture> pictures;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "ad", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @ToString.Exclude
    private Set<Rental> rentals;

    // one-to-many connection with review.
    // When an ad is removed, its corresponding reviews are also removed.
    // When ad is persisted, the reviews are also updated
    @OneToMany(mappedBy = "ad", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @ToString.Exclude
    private Set<Review> reviews; // todo create a rating for user such taht the rating does not get removed from the user

    // Many-to-many connection with Date. Date is parent in this case.
    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "ads")
    @ToString.Exclude
    private Set<CalendarDate> dates = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Ad ad = (Ad) o;
        return id != null && Objects.equals(id, ad.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

package com.example.idatt2106_2022_05_backend.model;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
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

    // Coordinates latitude
    @Column(name="LAT")
    private double lat;

    // Coordinates longitude
    @Column(name="LNG")
    private double lng;

    // Created timestamp --> for use in calculating ad-expiration
    @Temporal( TemporalType.TIMESTAMP )
    @CreationTimestamp
    @Column(name = "created")
    private LocalDate created;

    // Is nullable
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "ad")
    private Set<Picture> pictures;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // one-to-many connection with review.
    // When an ad is removed, its corresponding reviews are also removed.
    // When ad is persisted, the reviews are also updated
    @OneToMany(mappedBy = "ad", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<Review> reviews;

    // Many-to-many connection with Date. Date is parent in this case.
    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "ads")
    private Set<CalendarDate> dates = new HashSet<>();
}

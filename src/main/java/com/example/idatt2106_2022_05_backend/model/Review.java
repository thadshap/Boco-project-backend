package com.example.idatt2106_2022_05_backend.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @Column(name = "rating")
    private int rating;

    @Column(name = "description")
    private String description;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @PreRemove
    private void removeRelationships() {
        if (ad != null) {
            setAd(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Review review = (Review) o;
        return id != null && Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

package com.example.idatt2106_2022_05_backend.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
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
    @SequenceGenerator(name = "review_sequence", sequenceName = "review_sequence", allocationSize = 1)
    @GeneratedValue(generator = "review_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @Column(name = "rating")
    private int rating;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id")
    private Ad ad;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Review review = (Review) o;
        return id != null && Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

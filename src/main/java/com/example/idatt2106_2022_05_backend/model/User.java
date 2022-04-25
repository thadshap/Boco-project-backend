package com.example.idatt2106_2022_05_backend.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class User {

    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(generator = "user_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "userId") //todo change to auto
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @NotNull
    private String password;

    private String role = "USER";

    private boolean verified = false;

    private double rating;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private UserVerificationToken userVerificationToken;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private ResetPasswordToken resetPasswordToken;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "picture_id", referencedColumnName = "picture_id")
    private Picture picture;

    // private Set<UserGroup> userGroup

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Rental> rentalsOwned;

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Rental> rentalsBorrowed;

    // PS: These reviews are those that are WRITTEN by this user (not owned)
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Review> reviews;

    // One to many relationship w/ ad
    @OneToMany(cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, mappedBy = "user")
    @ToString.Exclude
    private Set<Ad> ads = new HashSet<>();

    public void setAd(Ad newAd) {
        ads.add(newAd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

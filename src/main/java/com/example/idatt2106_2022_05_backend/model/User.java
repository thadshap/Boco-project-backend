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
@Data
@AllArgsConstructor
public class User {

    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(generator = "user_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
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

    @OneToOne(fetch = FetchType.EAGER)
    private UserVerificationToken userVerificationToken;

    @OneToOne(fetch = FetchType.EAGER)
    private ResetPasswordToken resetPasswordToken;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "picture_id", referencedColumnName = "picture_id")
    private Picture picture;

    //
    // private Set<UserGroup> userGroup

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Rental> rentalsOwned;

    @OneToMany(mappedBy = "borrower", fetch = FetchType.EAGER)
    private List<Rental> rentalsBorrowed;

    // One to many relationship w/ ad
    @OneToMany(cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, mappedBy = "user")
    private Set<Ad> ads = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

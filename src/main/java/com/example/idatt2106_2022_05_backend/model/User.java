package com.example.idatt2106_2022_05_backend.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "user_sequence",
            strategy = GenerationType.SEQUENCE)
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

    //@OneToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "picture_id", referencedColumnName = "picture_id")
//    private Picture picture;


    //
    //private Set<UserGroup> userGroup

    //@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    //private Set<Rental> owner

    //@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    //private Set<Rental> borrower

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

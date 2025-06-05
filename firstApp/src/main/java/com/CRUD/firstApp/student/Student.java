// Student.java
package com.CRUD.firstApp.student;

import com.CRUD.firstApp.auth.Role;
import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.instructors.Instructors;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "student")
// <-- À chaque occurrence ultérieure, Jackson renverra juste { "id": … }
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Student implements UserDetails {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private int id;

    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String email;
    private String phone;

    @Embedded
    private Address address;

    private Role role;
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_instructor_subscription",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    // On ne souhaite pas exposer la liste complète des instructeurs depuis Student,
    // car l’entité Instructor possède elle-même une collection de Student.
    // Le fait de mettre @JsonIgnore ici coupe la boucle côté « étudiant → instructeur ».
    @JsonIgnore
    private Set<Instructors> instructors = new HashSet<>();


    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    // Courses est annoté lui aussi avec @JsonIdentityInfo (voir plus bas),
    // donc ici on n’ajoute pas @JsonIgnore. Jackson saura qu’après la première
    // occurrence complète d’un Course, il n’aura qu’à émettre { "id": … }.
    private Set<Courses> courses = new HashSet<>();

    // ---------- Implémentation UserDetails ----------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() { return email; }

    @Override
    public String getPassword() { return password; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked()  { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
    // -----------------------------------------------
}

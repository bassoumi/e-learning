package com.CRUD.firstApp.student;


import com.CRUD.firstApp.auth.Role;
import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.instructors.Instructors;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "student")
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

    @ManyToMany
    @JoinTable(
            name = "student_instructor_subscription",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private Set<Instructors> instructors = new HashSet<>();

    @ManyToMany(mappedBy = "students")
    private Set<Courses> courses = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

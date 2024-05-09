package com.example.BEFoodrecommendationapplication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name="name", length = 150)
    private String name;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="height")
    private Float height;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "dietary_goal")
    private Integer dietaryGoal;

    @Column(name="password", columnDefinition = "TEXT", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name="created_at")
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;


    @Column(name = "gender")
    private String gender;

    @Column(name = "daily_activities")
    private String dailyActivities;

    @Column(name = "meals")
    private Integer meals;

    @Column(name = "birthday")
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate birthday;

    @Column(name = "active")
    private boolean active;

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_generated_time")
    private LocalDateTime otpGeneratedTime;


    @ManyToMany
    @JoinTable(
            name = "user_ingredient",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> ingredients;

    public void setMeals(Integer meals) {
        if (meals != 3 && meals != 4 && meals != 5) {
            throw new IllegalArgumentException("Invalid value for meals");
        }
        this.meals = meals;
    }
    public void setDietaryGoal(Integer dietaryGoal) {
        if (dietaryGoal != 1 && dietaryGoal != 2 && dietaryGoal != 3) {
            throw new IllegalArgumentException("Invalid value for dietary goal");
        }
        this.dietaryGoal = dietaryGoal;
    }
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.USER.toString()));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
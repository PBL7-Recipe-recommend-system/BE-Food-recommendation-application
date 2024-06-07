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
import java.time.Period;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "height")
    private Float height;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "dietary_goal")
    private Integer dietaryGoal;

    @Column(name = "password", columnDefinition = "TEXT", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "created_at")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;


    @Column(name = "gender")
    private String gender;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "daily_activities")
    private String dailyActivities;

    @Column(name = "meals")
    private Integer meals;

    @Column(name = "birthday")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthday;

    @Column(name = "active")
    private boolean active;


    @Setter
    @Column(name = "is_custom_plan")
    private boolean isCustomPlan;

    @Column(name = "otp")
    @JsonIgnore
    private String otp;

    @Column(name = "otp_generated_time")
    @JsonIgnore
    private LocalDateTime otpGeneratedTime;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Token> tokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<SavedRecipe> savedRecipes;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<UserIncludeIngredient> includeIngredients;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<UserExcludeIngredient> excludeIngredients;

    public int calculateAge() {
        if (this.birthday == null) {
            return 0;
        }
        LocalDate now = LocalDate.now();
        Period period = Period.between(this.birthday, now);
        return period.getYears();
    }

    public float calculateBmi() {
        if (this.weight == null || this.height == null) {
            return 0;
        }
        float heightInMeters = this.height / 100;
        float bmi = this.weight / (heightInMeters * heightInMeters);
        return Math.round(bmi * 100.0) / 100.0f;
    }

    public float calculateBmr() {
        if (this.weight == null || this.height == null || this.birthday == null) {
            return 0;
        }
        int age = this.calculateAge();
        float bmr;
        if (this.gender.equals("Male")) {
            bmr = 10 * this.weight + 6.25f * this.height - 5 * age + 5;
        } else {
            bmr = 10 * this.weight + 6.25f * this.height - 5 * age - 161;
        }
        return bmr;
    }

    public float caloriesCalculator() {
        if (this.dailyActivities == null) {
            return 0;
        }
        String[] activities = {"Little/no exercise", "Light exercise", "Moderate exercise (3-5 days/wk)", "Very active (6-7 days/wk)", "Extra active (very active & physical job)"};
        float[] weights = {1.2f, 1.375f, 1.55f, 1.725f, 1.9f};
        int activityIndex = Arrays.asList(activities).indexOf(this.dailyActivities);
        float weight = weights[activityIndex];

        return this.calculateBmr() * weight;
    }

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
        return Arrays.stream(Role.values())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
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
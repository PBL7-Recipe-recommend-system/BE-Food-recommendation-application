package com.example.BEFoodrecommendationapplication.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_diet_restriction")
public class UserDietRestriction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "restriction_id")
    private DietRestriction dietRestriction;

    // getters and setters
}
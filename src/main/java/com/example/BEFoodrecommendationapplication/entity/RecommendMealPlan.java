package com.example.BEFoodrecommendationapplication.entity;


import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "recommend_meal_plan")
public class RecommendMealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommend_meal_plan_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "daily_calorie")
    private Integer dailyCalorie;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "recommendMealPlan")
    private Set<RecommendMealPlanRecipe> recommendMealPlanRecipes;

}
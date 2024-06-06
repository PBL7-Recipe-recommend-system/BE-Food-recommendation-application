package com.example.BEFoodrecommendationapplication.dto;

import java.util.HashMap;
import java.util.Map;

public class DailyNutritionResponse {
    private Map<String, MealNutrition> meals;
    private Float recommendWaterIntake;
    private Float waterIntake;

    // Constructor to initialize the meals map and set default values for water intake
    public DailyNutritionResponse() {
        this.meals = new HashMap<>();
        this.recommendWaterIntake = 0.0f;  // Default to 0, can be set later
        this.waterIntake = 0.0f;           // Default to 0, can be set later
    }

    // Adds nutrition information for a specific meal
    public void addMealNutrition(String mealName, MealNutrition nutrition) {
        this.meals.put(mealName, nutrition);
    }

    // Standard getters and setters
    public Map<String, MealNutrition> getMeals() {
        return meals;
    }

    public void setMeals(Map<String, MealNutrition> meals) {
        this.meals = meals;
    }

    public Float getRecommendWaterIntake() {
        return recommendWaterIntake;
    }

    public void setRecommendWaterIntake(Float recommendWaterIntake) {
        this.recommendWaterIntake = recommendWaterIntake;
    }

    public Float getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(Float waterIntake) {
        this.waterIntake = waterIntake;
    }
}

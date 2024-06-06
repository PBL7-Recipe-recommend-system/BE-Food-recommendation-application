package com.example.BEFoodrecommendationapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyNutritionResponse {
    private MealNutrition breakfast;
    private MealNutrition lunch;
    private MealNutrition dinner;

    private MealNutrition morningSnack;

    private MealNutrition afternoonSnack;

    private Float recommendWaterIntake;
    private Float waterIntake;

    private Integer recommendCalories;
    private Integer totalCalories;


}

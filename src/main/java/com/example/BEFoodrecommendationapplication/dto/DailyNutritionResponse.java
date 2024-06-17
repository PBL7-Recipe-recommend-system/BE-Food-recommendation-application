package com.example.BEFoodrecommendationapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyNutritionResponse {
    private List<MealNutrition> breakfast;
    private List<MealNutrition> lunch;
    private List<MealNutrition> dinner;

    private List<MealNutrition> morningSnack;

    private List<MealNutrition> afternoonSnack;

    private Float recommendWaterIntake;
    private Float waterIntake;

    private Integer recommendCalories;
    private Integer totalCalories;


}

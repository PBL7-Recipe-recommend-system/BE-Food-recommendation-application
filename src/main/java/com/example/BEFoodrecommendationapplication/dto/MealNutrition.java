package com.example.BEFoodrecommendationapplication.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealNutrition {
    private Integer recipeId;
    private String name;
    private Integer servings;

    private Float calories;
    private Float fatContent;
    private Float sodiumContent;
    private Float carbohydrateContent;
    private Float fiberContent;
    private Float sugarContent;
    private Float proteinContent;
    private Float fiberPercentage;
    private Float sugarPercentage;
    private Float fatPercentage;
    private Float proteinPercentage;
    private Float carbohydratePercentage;

}
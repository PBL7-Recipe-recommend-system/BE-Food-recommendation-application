package com.example.BEFoodrecommendationapplication.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetCookedRecipeDto {
    private Integer recipeId;
    private Integer servingSize;
    private String meal;
}

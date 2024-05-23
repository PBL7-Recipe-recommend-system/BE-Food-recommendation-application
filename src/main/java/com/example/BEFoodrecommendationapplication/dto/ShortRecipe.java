package com.example.BEFoodrecommendationapplication.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortRecipe {

    private int recipeId;

    private String name;

    private String totalTime;

    private String image;

    private int calories;
}

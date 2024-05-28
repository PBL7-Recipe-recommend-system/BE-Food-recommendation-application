package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private Integer totalCalories;

    @JsonIgnore
    private Double protein;

    @JsonIgnore
    private Double fat;
}

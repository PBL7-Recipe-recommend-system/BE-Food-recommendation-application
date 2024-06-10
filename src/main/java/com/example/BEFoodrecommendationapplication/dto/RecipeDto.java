package com.example.BEFoodrecommendationapplication.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {

    private Integer recipeId;

    private String name;

    private Integer authorId;

    private String authorName;

    private String cookTime;

    private String prepTime;

    private String totalTime;

    private String datePublished;

    private String description;

    private List<String> images;

    private String recipeCategory;

    private List<String> keywords;

    private List<String> recipeIngredientsQuantities;

    private List<String> recipeIngredientsParts;

    private Integer aggregatedRatings;

    private Integer reviewCount;

    private Float calories;

    private Float fatContent;

    private Float saturatedFatContent;

    private Float cholesterolContent;

    private Float sodiumContent;

    private Float carbonhydrateContent;

    private Float fiberContent;

    private Float sugarContent;

    private Float proteinContent;

    private Integer recipeServings;

    private String recipeYeild;

    private List<String> recipeInstructions;

    private boolean isSaved;
}



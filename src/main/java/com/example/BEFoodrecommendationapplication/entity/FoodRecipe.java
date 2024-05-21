package com.example.BEFoodrecommendationapplication.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "food_recipe")
public class FoodRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Integer recipeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "cook_time")
    private String cookTime;

    @Column(name = "prep_time")
    private String prepTime;

    @Column(name = "total_time")
    private String totalTime;

    @Column(name = "date_published", nullable = false)
    private Date datePublished;

    @Column(name = "description")
    private String description;

    @Column(name = "images")
    private String images;

    @Column(name = "recipe_category")
    private String recipeCategory;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "recipe_ingredients_quantities")
    private String recipeIngredientsQuantities;

    @Column(name = "recipe_ingredients_parts")
    private String recipeIngredientsParts;

    @Column(name = "aggregated_ratings")
    private Integer aggregatedRatings;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "calories")
    private Float calories;

    @Column(name = "fat_content")
    private Float fatContent;

    @Column(name = "saturated_fat_content")
    private Float saturatedFatContent;

    @Column(name = "cholesterol_content")
    private Float cholesterolContent;

    @Column(name = "sodium_content")
    private Float sodiumContent;

    @Column(name = "carbonhydrate_content")
    private Float carbonhydrateContent;

    @Column(name = "fiber_content")
    private Float fiberContent;

    @Column(name = "sugar_content")
    private Float sugarContent;

    @Column(name = "protein_content")
    private Float proteinContent;

    @Column(name = "recipe_servings")
    private Integer recipeServings;

    @Column(name = "recipe_instructions")
    private String recipeInstructions;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;


}
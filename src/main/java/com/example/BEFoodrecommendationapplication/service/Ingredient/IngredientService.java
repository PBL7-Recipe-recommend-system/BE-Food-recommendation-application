package com.example.BEFoodrecommendationapplication.service.Ingredient;

import com.example.BEFoodrecommendationapplication.dto.IngredientDto;

import java.util.List;

public interface IngredientService {
    List<IngredientDto> getRecipeIngredientsById(Integer id);
}

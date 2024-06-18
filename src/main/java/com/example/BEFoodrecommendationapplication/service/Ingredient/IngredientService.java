package com.example.BEFoodrecommendationapplication.service.Ingredient;

import com.example.BEFoodrecommendationapplication.dto.IngredientDto;
import com.example.BEFoodrecommendationapplication.dto.UpdateIngredientsRequest;

import java.util.List;

public interface IngredientService {
    List<IngredientDto> getRecipeIngredientsById(Integer id);

    UpdateIngredientsRequest updateRecipeIngredientNamesAndQuantities(Integer recipeId, UpdateIngredientsRequest request);

    List<IngredientDto> addIngredient(Integer recipeId, IngredientDto ingredientDTO);

    List<String> getAllIngredient(String name);

    List<String> getUserIngredients(Integer userId, String includeOrExclude);
}

package com.example.BEFoodrecommendationapplication.service.Ingredient;

import com.example.BEFoodrecommendationapplication.dto.IngredientDto;
import com.example.BEFoodrecommendationapplication.dto.UpdateIngredientsRequest;
import com.example.BEFoodrecommendationapplication.entity.Ingredient;

import java.util.List;
import java.util.Optional;

public interface IngredientService {
    List<IngredientDto> getRecipeIngredientsById(Integer id);

    UpdateIngredientsRequest updateRecipeIngredientNamesAndQuantities(Integer recipeId, UpdateIngredientsRequest request);

    List<IngredientDto> addIngredient(Integer recipeId, IngredientDto ingredientDTO);

    List<String> getAllIngredient(String name);

    List<String> getUserIngredients(Integer userId, String includeOrExclude);

    void addUserIngredients(Integer userId, List<String> ingredientNames, String includeOrExclude);

    void deleteUserIngredient(Integer userId, String ingredientName, String includeOrExclude);
}

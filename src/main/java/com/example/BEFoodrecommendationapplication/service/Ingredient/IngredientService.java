package com.example.BEFoodrecommendationapplication.service.Ingredient;

import com.example.BEFoodrecommendationapplication.dto.IngredientDto;
import com.example.BEFoodrecommendationapplication.dto.UpdateIngredientsRequest;

import java.util.List;

public interface IngredientService {
    List<IngredientDto> getRecipeIngredientsById(Integer id);

    UpdateIngredientsRequest updateRecipeIngredientNames(Integer recipeId, UpdateIngredientsRequest request);
}

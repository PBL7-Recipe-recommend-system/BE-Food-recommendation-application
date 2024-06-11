package com.example.BEFoodrecommendationapplication.service.FoodRecipe;

import com.example.BEFoodrecommendationapplication.dto.RecipeDto;
import com.example.BEFoodrecommendationapplication.dto.SearchResult;
import com.example.BEFoodrecommendationapplication.dto.SetCookedRecipeDto;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.UserCookedRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FoodRecipeService {

    Page<SearchResult> search(String name, String category, Integer rating, Integer dateRating, Pageable pageable, Integer userId);

    RecipeDto mapToDto(FoodRecipe foodRecipe, Integer userId);

    FoodRecipe findById(Integer id);

    Page<SearchResult> findPopularRecipes(int page, int size);

    SearchResult mapToSearchResult(FoodRecipe foodRecipe);

    void saveRecentSearch(Integer userId, FoodRecipe foodRecipe);

    void setRecipeAsCooked(Integer userId, SetCookedRecipeDto input);

    List<UserCookedRecipe> getCookedRecipesByUser(Integer userId);

    List<String> getRecipeInstructionById(Integer id);

    FoodRecipe addFoodRecipe(String name, Integer authorId);

    FoodRecipe updateFoodRecipe(Integer recipeId, RecipeDto recipeDto);
}

package com.example.BEFoodrecommendationapplication.service.FoodRecipe;

import com.example.BEFoodrecommendationapplication.dto.RecipeDto;
import com.example.BEFoodrecommendationapplication.dto.SearchResult;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FoodRecipeService {

     Page<SearchResult> search(String name, String category, Integer rating, Pageable pageable);

     RecipeDto mapToDto(FoodRecipe foodRecipe);

     FoodRecipe findById(Integer id);

     Page<SearchResult> findPopularRecipes(int page, int size);

     SearchResult mapToSearchResult(FoodRecipe foodRecipe);
}

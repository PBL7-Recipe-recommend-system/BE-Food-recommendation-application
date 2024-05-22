package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.SavedRecipe;
import com.example.BEFoodrecommendationapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {

    SavedRecipe findByUserAndRecipe(User user, FoodRecipe recipe);
}

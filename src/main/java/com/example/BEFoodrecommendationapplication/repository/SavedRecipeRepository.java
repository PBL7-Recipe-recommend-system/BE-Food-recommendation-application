package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.SavedRecipe;
import com.example.BEFoodrecommendationapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {

    SavedRecipe findByUserAndRecipe(User user, FoodRecipe recipe);

    @Query("SELECT s FROM SavedRecipe s WHERE s.user.id = :userId AND s.recipe.recipeId = :recipeId")
    Optional<SavedRecipe> findByUserIdAndRecipeId(@Param("userId") Integer userId, @Param("recipeId") Integer recipeId);

    List<SavedRecipe> findByUserId(Integer userId);
}

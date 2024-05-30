package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.entity.UserCookedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserCookedRecipeRepository extends JpaRepository<UserCookedRecipe, Integer> {
    Optional<UserCookedRecipe> findByUserAndRecipe(User user, FoodRecipe recipe);

    Optional<UserCookedRecipe> findByUserAndRecipeAndDate(User user, FoodRecipe recipe, LocalDate date);
}

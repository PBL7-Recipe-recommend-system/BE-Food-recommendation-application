package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.UserExcludeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExcludeIngredientRepository extends JpaRepository<UserExcludeIngredient, Integer> {
}
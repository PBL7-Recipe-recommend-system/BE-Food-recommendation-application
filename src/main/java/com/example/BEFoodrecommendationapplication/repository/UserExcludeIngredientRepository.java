package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.entity.UserExcludeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface UserExcludeIngredientRepository extends JpaRepository<UserExcludeIngredient, Integer> {
    UserExcludeIngredient findByUserAndIngredient(User user, Ingredient ingredient);
}
package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.entity.UserExcludeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserExcludeIngredientRepository extends JpaRepository<UserExcludeIngredient, Integer> {


    Optional<UserExcludeIngredient> findByUserAndIngredient(User user, Ingredient ingredient);

    List<UserExcludeIngredient> findAllByUserId(Integer userId);
}
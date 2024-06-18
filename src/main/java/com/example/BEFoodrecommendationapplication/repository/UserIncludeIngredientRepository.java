package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.entity.UserExcludeIngredient;
import com.example.BEFoodrecommendationapplication.entity.UserIncludeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserIncludeIngredientRepository extends JpaRepository<UserIncludeIngredient, Integer> {

    List<UserIncludeIngredient> findAllByUserId(Integer userId);

    Optional<UserIncludeIngredient> findByUserAndIngredient(User user, Ingredient ingredient);
}

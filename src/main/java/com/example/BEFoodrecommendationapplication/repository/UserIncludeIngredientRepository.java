package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.UserIncludeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface UserIncludeIngredientRepository extends JpaRepository<UserIncludeIngredient, Integer> {


}

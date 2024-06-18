package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.UserIncludeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface UserIncludeIngredientRepository extends JpaRepository<UserIncludeIngredient, Integer> {

    List<UserIncludeIngredient> findAllByUserId(Integer userId);
}

package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.RecommendMealPlan;
import com.example.BEFoodrecommendationapplication.entity.RecommendMealPlanRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface RecommendMealPlanRecipeRepository extends JpaRepository<RecommendMealPlanRecipe, Integer> {
    void deleteByRecommendMealPlan(RecommendMealPlan recommendMealPlan);
}

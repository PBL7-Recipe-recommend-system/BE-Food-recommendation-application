package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.RecommendMealPlan;
import com.example.BEFoodrecommendationapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface RecommendMealPlanRepository extends JpaRepository<RecommendMealPlan, Integer> {
    List<RecommendMealPlan> findByUser(User user);
}

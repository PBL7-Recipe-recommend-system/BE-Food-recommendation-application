package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.CustomMealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;

@RepositoryRestResource(exported = false)
public interface CustomMealPlanRepository extends JpaRepository<CustomMealPlan, Integer> {
    CustomMealPlan findByUserId(Integer userId);

    CustomMealPlan findByUserIdAndDate(Integer userId, LocalDate date);
}

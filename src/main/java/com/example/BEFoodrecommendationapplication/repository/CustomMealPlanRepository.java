package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.CustomMealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(exported = false)
public interface CustomMealPlanRepository extends JpaRepository<CustomMealPlan, Integer> {
    CustomMealPlan findByUserId(Integer userId);

    CustomMealPlan findByUserIdAndDate(Integer userId, LocalDate date);

    List<CustomMealPlan> findAllByUserIdAndDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);
}

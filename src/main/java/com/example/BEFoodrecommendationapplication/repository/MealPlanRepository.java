package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.MealPlan;
import com.example.BEFoodrecommendationapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(exported = false)
public interface MealPlanRepository extends JpaRepository<MealPlan, Integer> {
    MealPlan findByUserAndDate(User user, LocalDate date);

    @Query("SELECT m FROM MealPlan m WHERE m.user.id = :userId AND m.date >= :today")
    List<MealPlan> findCurrentMealPlans(Integer userId, LocalDate today);

    List<MealPlan> findAllByUser(User user);
}

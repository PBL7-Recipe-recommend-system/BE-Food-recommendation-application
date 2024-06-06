package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.WaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Integer> {


    Optional<WaterIntake> findByUserIdAndDate(Integer userId, LocalDate date);
}
package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.WaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Integer> {


    Optional<WaterIntake> findByUserIdAndDate(Integer userId, LocalDate date);
}

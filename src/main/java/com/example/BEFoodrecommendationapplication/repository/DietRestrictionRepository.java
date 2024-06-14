package com.example.BEFoodrecommendationapplication.repository;


import com.example.BEFoodrecommendationapplication.entity.DietRestriction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietRestrictionRepository extends JpaRepository<DietRestriction, Integer> {

    DietRestriction findByType(String restrictionType);
}

package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    @Query(value = "SELECT * FROM ingredient LIMIT 100", nativeQuery = true)
    List<Ingredient> find100Ingredients();

    List<Ingredient> findByNameIn(List<String> names);
}

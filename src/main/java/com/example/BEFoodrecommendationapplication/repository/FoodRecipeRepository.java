package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface FoodRecipeRepository extends JpaRepository<FoodRecipe, Integer>, JpaSpecificationExecutor<FoodRecipe> {
    @Query("SELECT r FROM FoodRecipe r " +
            "WHERE r.aggregatedRatings > 3 " +
            "ORDER BY r.aggregatedRatings DESC, r.reviewCount DESC")
    Page<FoodRecipe> findPopularRecipes(Pageable pageable);

    @Query("SELECT DISTINCT f.recipeCategory FROM FoodRecipe f")
    List<String> findDistinctCategories();

    @Query("SELECT f.recipeCategory, COUNT(f) FROM FoodRecipe f GROUP BY f.recipeCategory ORDER BY COUNT(f) DESC")
    List<Object[]> findAllCategoriesOrderByPopularity();

}

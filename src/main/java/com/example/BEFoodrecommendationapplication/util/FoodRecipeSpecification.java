package com.example.BEFoodrecommendationapplication.util;

import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class FoodRecipeSpecification {

    public static Specification<FoodRecipe> nameStartsWith(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), name.toLowerCase() + "%");
    }

    public static Specification<FoodRecipe> categoryContains(String category) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("recipeCategory")), "%" + category.toLowerCase() + "%");
    }

    public static Specification<FoodRecipe> caloriesBetween(Integer minCalories, Integer maxCalories) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("calories"), minCalories, maxCalories);
    }

    public static Specification<FoodRecipe> ratingIs(Integer rating) {
        return (root, query, cb) -> cb.equal(root.get("aggregatedRatings"), rating);
    }

    public static Specification<FoodRecipe> keywordStartsWith(String keyword) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("keywords")), keyword.toLowerCase() + "%");
    }

    public static Specification<FoodRecipe> caloriesBetween(Float minCalories, Float maxCalories) {
        return (root, query, cb) -> {
            if (minCalories != null && maxCalories != null) {
                return cb.between(root.get("calories"), minCalories, maxCalories);
            } else if (minCalories != null) {
                return cb.greaterThanOrEqualTo(root.get("calories"), minCalories);
            } else if (maxCalories != null) {
                return cb.lessThanOrEqualTo(root.get("calories"), maxCalories);
            } else {
                return cb.conjunction();
            }
        };
    }

    public static Specification<FoodRecipe> proteinContentBetween(Float minProteinContent, Float maxProteinContent) {
        return (root, query, cb) -> {
            if (minProteinContent != null && maxProteinContent != null) {
                return cb.between(root.get("proteinContent"), minProteinContent, maxProteinContent);
            } else if (minProteinContent != null) {
                return cb.greaterThanOrEqualTo(root.get("proteinContent"), minProteinContent);
            } else if (maxProteinContent != null) {
                return cb.lessThanOrEqualTo(root.get("proteinContent"), maxProteinContent);
            } else {
                return cb.conjunction();
            }
        };
    }

    public static Specification<FoodRecipe> fatContentBetween(Float minFatContent, Float maxFatContent) {
        return (root, query, cb) -> {
            if (minFatContent != null && maxFatContent != null) {
                return cb.between(root.get("fatContent"), minFatContent, maxFatContent);
            } else if (minFatContent != null) {
                return cb.greaterThanOrEqualTo(root.get("fatContent"), minFatContent);
            } else if (maxFatContent != null) {
                return cb.lessThanOrEqualTo(root.get("fatContent"), maxFatContent);
            } else {
                return cb.conjunction();
            }
        };
    }

    public static Specification<FoodRecipe> carbohydrateContentBetween(Float minCarbContent, Float maxCarbContent) {
        return (root, query, cb) -> {
            if (minCarbContent != null && maxCarbContent != null) {
                return cb.between(root.get("carbonhydrateContent"), minCarbContent, maxCarbContent);
            } else if (minCarbContent != null) {
                return cb.greaterThanOrEqualTo(root.get("carbonhydrateContent"), minCarbContent);
            } else if (maxCarbContent != null) {
                return cb.lessThanOrEqualTo(root.get("carbonhydrateContent"), maxCarbContent);
            } else {
                return cb.conjunction();
            }
        };
    }
}


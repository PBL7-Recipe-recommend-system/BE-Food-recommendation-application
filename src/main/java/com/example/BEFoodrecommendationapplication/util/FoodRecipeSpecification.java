package com.example.BEFoodrecommendationapplication.util;

import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component
@NoArgsConstructor
public class FoodRecipeSpecification {

    public static Specification<FoodRecipe> nameContains(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<FoodRecipe> categoryContains(String category) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("recipeCategory")), "%" + category.toLowerCase() + "%");
    }

    public static Specification<FoodRecipe> ratingIs(Integer rating) {
        return (root, query, cb) -> cb.equal(root.get("aggregatedRatings"), rating);
    }
    public static Specification<FoodRecipe> keywordContains(String keyword) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("keywords")), "%" + keyword.toLowerCase() + "%");
    }

}

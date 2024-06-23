package com.example.BEFoodrecommendationapplication.service.FoodRecipe;

import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class FoodRecipeRowMapper implements RowMapper<FoodRecipe> {
    @Override
    public FoodRecipe mapRow(ResultSet rs, int rowNum) throws SQLException {
        FoodRecipe foodRecipe = new FoodRecipe();
        // Map ResultSet columns to FoodRecipe fields
        foodRecipe.setRecipeId(rs.getInt("recipe_id"));
        foodRecipe.setName(rs.getString("name"));
        foodRecipe.setRecipeCategory(rs.getString("recipe_category"));
        foodRecipe.setAggregatedRatings(rs.getInt("aggregated_ratings"));
        foodRecipe.setKeywords(rs.getString("keywords"));
        foodRecipe.setDatePublished(Date.from(rs.getTimestamp("date_published").toInstant()));
        foodRecipe.setReviewCount(rs.getInt("review_count"));
        // Add other fields as needed
        return foodRecipe;
    }
}


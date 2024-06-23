CREATE INDEX idx_foodrecipe_name ON food_recipe (name);
CREATE INDEX idx_foodrecipe_category ON food_recipe (recipe_category);
CREATE INDEX idx_foodrecipe_rating ON food_recipe (aggregated_ratings);
CREATE INDEX idx_foodrecipe_keywords ON food_recipe (keywords(255));
CREATE INDEX idx_foodrecipe_datepublished ON food_recipe (date_published);

-- Create composite indexes
CREATE INDEX idx_foodrecipe_name_category ON food_recipe (name, recipe_category);
CREATE INDEX idx_foodrecipe_category_rating ON food_recipe (recipe_category, aggregated_ratings);
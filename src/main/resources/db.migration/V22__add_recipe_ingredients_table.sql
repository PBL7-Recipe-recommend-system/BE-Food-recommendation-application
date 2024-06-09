CREATE TABLE recipe_ingredients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ingredient_id INT,
    recipe_id INT,
    quantity Double,
    unit VARCHAR(255),
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id),
    FOREIGN KEY (recipe_id) REFERENCES food_recipe(recipe_id)
);
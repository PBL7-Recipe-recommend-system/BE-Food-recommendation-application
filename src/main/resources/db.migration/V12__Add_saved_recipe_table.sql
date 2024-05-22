CREATE TABLE saved_recipes (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               user_id INT,
                               recipe_id INT,
                               saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES user(user_id),
                               FOREIGN KEY (recipe_id) REFERENCES food_recipe(recipe_id),
                               UNIQUE(user_id, recipe_id)
);
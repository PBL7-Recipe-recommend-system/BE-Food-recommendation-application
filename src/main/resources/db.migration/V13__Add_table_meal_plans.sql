DROP TABLE meal_recipe;
DROP TABLE meal;
DROP TABLE diet_plan;
CREATE TABLE meal_plans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    breakfast_id INT,
    lunch_id INT,
    dinner_id INT,
    snack1_id INT,
    snack2_id INT,
    date DATE NOT NULL,
    daily_calorie INT,
    total_calorie INT,
    description VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (breakfast_id) REFERENCES food_recipe(recipe_id),
    FOREIGN KEY (lunch_id) REFERENCES food_recipe(recipe_id),
    FOREIGN KEY (dinner_id) REFERENCES food_recipe(recipe_id),
    FOREIGN KEY (snack1_id) REFERENCES food_recipe(recipe_id),
    FOREIGN KEY (snack2_id) REFERENCES food_recipe(recipe_id)
);
CREATE TABLE recommend_meal_plan (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            user_id INT NOT NULL,
                            date DATE NOT NULL,
                            daily_calorie INT,
                            description VARCHAR(255),
                            FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE recommend_meal_plan_recipes (
                                   meal_plan_id INT NOT NULL,
                                   recipe_id INT NOT NULL,
                                   meal_type ENUM('breakfast', 'lunch', 'dinner', 'morningSnack', 'afternoonSnack') NOT NULL,
                                    is_cook BOOLEAN DEFAULT FALSE,
                                   FOREIGN KEY (meal_plan_id) REFERENCES meal_plans(id),
                                   FOREIGN KEY (recipe_id) REFERENCES food_recipe(recipe_id),
                                   PRIMARY KEY (meal_plan_id, recipe_id, meal_type)
);
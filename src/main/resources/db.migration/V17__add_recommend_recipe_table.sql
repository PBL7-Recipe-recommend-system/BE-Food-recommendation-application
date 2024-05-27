CREATE TABLE recommend_meal_plan (
                            recommend_meal_plan_id INT PRIMARY KEY AUTO_INCREMENT,
                            user_id INT NOT NULL,
                            date DATE NOT NULL,
                            daily_calorie INT,
                            description VARCHAR(255),
                            FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE recommend_meal_plan_recipes (
                                    recommend_meal_plan_id INT NOT NULL,
                                   recipe_id INT NOT NULL,
                                   meal_type ENUM('breakfast', 'lunch', 'dinner', 'morningSnack', 'afternoonSnack') NOT NULL,
                                    is_cook BOOLEAN DEFAULT FALSE,
                                   FOREIGN KEY (recommend_meal_plan_id) REFERENCES recommend_meal_plan(recommend_meal_plan_id),
                                   FOREIGN KEY (recipe_id) REFERENCES food_recipe(recipe_id),
                                   PRIMARY KEY (recommend_meal_plan_id, recipe_id, meal_type)
);
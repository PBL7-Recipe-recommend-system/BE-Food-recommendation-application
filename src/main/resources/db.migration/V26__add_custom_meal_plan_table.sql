CREATE TABLE custom_meal_plan (
                                     custom_meal_plan_id INT PRIMARY KEY AUTO_INCREMENT,
                                     user_id INT NOT NULL,
                                     date DATE NOT NULL,
                                     daily_calorie INT,
                                     meal_count INT,
                                     description VARCHAR(255),
                                     FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE custom_meal_plan_recipes (
                                            id INT PRIMARY KEY AUTO_INCREMENT,
                                             custom_meal_plan_id INT NOT NULL,
                                             recipe_id INT NOT NULL,
                                             meal_type ENUM('breakfast', 'lunch', 'dinner', 'morningSnack', 'afternoonSnack') NOT NULL,
                                             FOREIGN KEY (custom_meal_plan_id) REFERENCES custom_meal_plan(custom_meal_plan_id),
                                             FOREIGN KEY (recipe_id) REFERENCES food_recipe(recipe_id)

);


























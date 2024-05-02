DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `food_recipe`;
DROP TABLE IF EXISTS `meal`;
DROP TABLE IF EXISTS `meal_recipe`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `diet_restriction`;
DROP TABLE IF EXISTS `user_diet_restriction`;

CREATE TABLE user (
                      user_id INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255),
                      email VARCHAR(50) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      weight FLOAT,
                      height FLOAT,
                      gender ENUM('MALE', 'FEMALE') NULL,
                      birthday DATE NULL,
                      daily_activities VARCHAR(255) NULL,
                      dietary_goal INT,
                      meals INT,
                      role ENUM('ADMIN', 'USER'),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE food_recipe (
                             recipe_id INT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             author_id INT NOT NULL,
                             author_name VARCHAR(255) NOT NULL,
                             cook_time VARCHAR(10),
                             prep_time VARCHAR(10),
                             total_time VARCHAR(20),
                             date_published DATETIME NOT NULL,
                             description TEXT,
                             images TEXT,
                             recipe_category VARCHAR(255),
                             keywords TEXT,
                             recipe_ingredients_quantities TEXT,
                             recipe_ingredients_parts TEXT,
                             aggregated_ratings INT,
                             review_count INT,
                             calories FLOAT,
                             fat_content FLOAT,
                             saturated_fat_content FLOAT,
                             cholesterol_content FLOAT,
                             sodium_content FLOAT,
                             carbonhydrate_content FLOAT,
                             fiber_content FLOAT,
                             sugar_content FLOAT,
                             protein_content FLOAT,
                             recipe_servings INT,
                             recipe_yeild VARCHAR(255),
                             recipe_instructions TEXT,
                             FOREIGN KEY (author_id) REFERENCES user(user_id)
);

CREATE TABLE diet_plan (
                           plan_id INT AUTO_INCREMENT PRIMARY KEY,
                           user_id INT NOT NULL,
                           daily_calorie_goal float,
                           daily_protein_goal float,
                           daily_fat_goal float,
                           daily_carbs_goal float,
                           total_calorie float,
                           total_fat float,
                           total_carbs float,
                           total_protein float,
                           start DATE,
                           end DATE,
                           description TEXT,
                           FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE meal (
                       meal_id INT AUTO_INCREMENT PRIMARY KEY,
                       plan_id INT NOT NULL,
                       meal_name VARCHAR(100),
                       meal_time TIME,
                       FOREIGN KEY (plan_id) REFERENCES diet_plan(plan_id)
);
CREATE TABLE meal_recipe (
                              meal_id INT,
                              recipe_id INT,
                              serving_size INT,
                              PRIMARY KEY (meal_id, recipe_id),
                              FOREIGN KEY (meal_id) REFERENCES meal(meal_id),
                              FOREIGN KEY (recipe_id) REFERENCES food_recipe(recipe_id)
);
CREATE TABLE review (
                        review_id INT AUTO_INCREMENT PRIMARY KEY,
                        recipe_id INT NOT NULL,
                        user_id INT NOT NULL,
                        rating INT CHECK (rating >= 1 AND rating <= 5),
                        comment TEXT,
                        review_date DATE,
                        FOREIGN KEY (recipe_id) REFERENCES food_recipe(recipe_id),
                        FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE diet_restriction (
                                  restriction_id INT AUTO_INCREMENT PRIMARY KEY,
                                  restriction_type VARCHAR(100),
                                  description TEXT

);
CREATE TABLE user_diet_restriction (
                                       user_id INT,
                                       restriction_id INT,
                                       PRIMARY KEY (user_id, restriction_id),
                                       FOREIGN KEY (user_id) REFERENCES user(user_id),
                                       FOREIGN KEY (restriction_id) REFERENCES diet_restriction(restriction_id)
);
CREATE TABLE ingredient (
                             ingredient_id INT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(100) NOT NULL,
                             Description TEXT
);
CREATE TABLE  user_ingredient (
                                 user_id INT,
                                 ingredient_id INT,
                                 PRIMARY KEY (user_id, ingredient_id),
                                 FOREIGN KEY (user_id) REFERENCES user(user_id),
                                 FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id)
);
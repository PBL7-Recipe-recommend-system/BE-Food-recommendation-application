ALTER TABLE `food_recommendation_db`.`user`
    CHANGE COLUMN `email` `email` VARCHAR(50) NULL ,
    CHANGE COLUMN `password` `password` VARCHAR(255) NULL ;

CREATE TABLE recent_search (
                              id INTEGER AUTO_INCREMENT PRIMARY KEY,
                              recipe_id INTEGER,
                              user_id INTEGER,
                              timestamp TIMESTAMP,
                              FOREIGN KEY (recipe_id) REFERENCES food_recipe(recipe_id),
                              FOREIGN KEY (user_id) REFERENCES user(user_id)
);
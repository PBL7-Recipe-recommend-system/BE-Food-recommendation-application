CREATE TABLE user_cooked_recipes
(
    id           INT AUTO_INCREMENT,
    user_id      INT NOT NULL,
    recipe_id    INT NOT NULL,
    is_cooked    BOOLEAN DEFAULT FALSE,
    date         DATE,
    serving_size INT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user (user_id),
    FOREIGN KEY (recipe_id) REFERENCES food_recipe (recipe_id),
    UNIQUE (user_id, recipe_id, date)
);
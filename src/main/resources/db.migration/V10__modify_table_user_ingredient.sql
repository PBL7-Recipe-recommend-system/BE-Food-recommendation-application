DROP TABLE user_ingredient;


CREATE TABLE user_exclude_ingredient (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    ingredient_id INT,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id)
);
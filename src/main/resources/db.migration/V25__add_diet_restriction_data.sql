-- Drop the tables
DROP TABLE IF EXISTS user_diet_restriction;
DROP TABLE IF EXISTS diet_restriction;

-- Recreate the diet_restriction table with an auto-incremented primary key
CREATE TABLE diet_restriction (
                                  restriction_id INT AUTO_INCREMENT PRIMARY KEY,
                                  restriction_type VARCHAR(100),
                                  description TEXT
);

-- Recreate the user_diet_restriction table with a new auto-incremented primary key
CREATE TABLE user_diet_restriction (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       user_id INT,
                                       restriction_id INT,
                                       FOREIGN KEY (user_id) REFERENCES user(user_id),
                                       FOREIGN KEY (restriction_id) REFERENCES diet_restriction(restriction_id)
);

INSERT INTO diet_restriction (restriction_type, description)
VALUES ('heart disease', 'Dietary restrictions for individuals with heart disease'),
       ('diabetes', 'Dietary restrictions for individuals with diabetes'),
       ('hypertension', 'Dietary restrictions for individuals with hypertension'),
       ('obesity', 'Dietary restrictions for individuals with obesity');
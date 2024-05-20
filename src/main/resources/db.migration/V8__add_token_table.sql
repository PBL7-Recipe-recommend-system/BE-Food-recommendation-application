
CREATE TABLE token (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       token VARCHAR(255),
                       is_logged_out BOOLEAN,
                       user_id INT,
                       FOREIGN KEY (user_id) REFERENCES user(user_id)
);
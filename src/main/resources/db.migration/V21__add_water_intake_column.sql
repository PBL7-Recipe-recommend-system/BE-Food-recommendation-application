CREATE TABLE water_intake (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    amount FLOAT,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
        `id` INT NOT NULL AUTO_INCREMENT,
        `name` varchar(150) DEFAULT NULL,
        `email` varchar(255) NOT NULL,
        `password` text NOT NULL,
        `created_at` timestamp,
        `role` ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `token`;
CREATE TABLE `token` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(255) NOT NULL DEFAULT 'BEARER',
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expired BOOLEAN NOT NULL DEFAULT FALSE,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE SET NULL
);
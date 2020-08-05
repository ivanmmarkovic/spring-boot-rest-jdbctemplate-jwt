CREATE TABLE users
(
    `id`       INT(11) NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(100) NOT NULL,
    `password` VARCHAR(100) NOT NULL,
    `enabled`  boolean     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `username_unique` (`username`)
);

CREATE TABLE user_authorities
(
    `id`        INT(11) NOT NULL AUTO_INCREMENT,
    `user_id`   INT(11) NOT NULL,
    `authority` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `username_authorities_unique` (`user_id`, `authority`)
);

ALTER TABLE `user_authorities`
    ADD CONSTRAINT `fk_authorities`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

CREATE TABLE `article` (
    `id` INT(11) PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(70) NOT NULL,
    `body` TEXT(2000) NOT NULL,
    `modified_at` TIMESTAMP,
    `created_at` TIMESTAMP,
    `user_id` INT(11),
    `username` VARCHAR(100),
    FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
);

CREATE TABLE `comment` (
    `id` INT(11) PRIMARY KEY AUTO_INCREMENT,
    `body` TEXT(2000) NOT NULL,
    `modified_at` TIMESTAMP,
    `created_at` TIMESTAMP,
    `article_id` int DEFAULT NULL,
    `user_id` INT(11),
    `username` VARCHAR(100),
    FOREIGN KEY(`article_id`) REFERENCES `article`(`id`) ON DELETE SET NULL,
    FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
);

# spring-boot-rest-jdbctemplate-jwt


Spring Boot REST project using H2 and JWT.

For testing application use [Postman](https://www.getpostman.com/) or [Insomnia](https://insomnia.rest/).

You can import project as Maven project.

Method | URL | description | access
-------|---- | ------------|--------
POST      |/login                               | login                        | all
GET       |/articles                            | get all articles             | all
GET       |/articles/{id}                       | get article with comments    | all
POST      |/articles                            | create new article           | user
PATCH     |/articles/{id}                       | update article               | user
DELETE    |/articles/{id}                       | delete article               | admin or user
POST      |/articles/{articleId}/comments       | create new comment           | user
DELETE    |/articles/{articleId}/comments/{id}  | delete comment               | admin or user
POST      |/users                               | create new user              | all
PATCH     |/users/{id}                          | update user                  | user 
DELETE    |/users/{id}                          | delete user                  | admin or user
POST      |/admins                              | create new admin             | admin
PATCH     |/admins/{id}                         | update admin                 | admin 
DELETE    |/admins/{id}                         | delete admin                 | admin



JSON format when login:
```
{
    "username": "yourUsername",
    "password": "yourPassword"
}
```
After login, user will get token. That token must be sent with every request in Authorization header. 
Authorization - "Bearer " + token

JSON format when adding new article:
```
{
    "title": "Java Spring",
    "body": "Place some text here"	
}
```

JSON format when adding new course or updating:
```
{
	"body": "Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can just run."
}
```

JSON format when adding new user or updating:
```
{
	"username": "John",
	"password": "secret"
}
```

When updating, you can ommit some properties. For example, if you want to update article:
```
{
	"title": "Spring"
}
```

JSON format when adding new admin or updating:
```
{
	"username": "John",
	"password": "secret"
}
```

Tables
```
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

```

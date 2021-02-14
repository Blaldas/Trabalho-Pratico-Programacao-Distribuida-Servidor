SET GLOBAL time_zone = '+0:00';
CREATE DATABASE IF NOT EXISTS `tppd2020`;
USE `tppd2020`;

DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `ficheiros`;
DROP TABLE IF EXISTS `canais`;
DROP TABLE IF EXISTS `channelChat`;
DROP TABLE IF EXISTS `mensagens`;

CREATE TABLE `users`(
    `id` int NOT NULL AUTO_INCREMENT,
    `name`        text NOT NULL,
    `password`    text NOT NULL,
    `path`        text NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ficheiros`(
    `id` int NOT NULL AUTO_INCREMENT,
    `data`        DATETIME DEFAULT CURRENT_TIMESTAMP,
	`nameChannel`  text NOT NULL,
    `fileName`     text NOT NULL,
	`path`         text NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `canais`(
    `id` int NOT NULL AUTO_INCREMENT,
    `owner`       text NOT NULL,
	`name`        text NOT NULL,
	`password`    text NOT NULL,
	`descricao`   text NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `channelChat`(
    `id`          INT AUTO_INCREMENT,
    `data`        DATETIME DEFAULT CURRENT_TIMESTAMP,
    `canal`       text NOT NULL,
    `autor`       text not null,
    `conteudo`    text not null,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `mensagens`(
    `id`         INT AUTO_INCREMENT,
    `data`       DATETIME DEFAULT CURRENT_TIMESTAMP,
    `autor`      text not null,
    `receptor`   text NOT NULL,
    `conteudo`   text not null,
    PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



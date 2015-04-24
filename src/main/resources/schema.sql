USE `appdb`;

/* Dropping tables */
DROP TABLE IF EXISTS `authorities`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `provinces`;
DROP TABLE IF EXISTS `regions`;
DROP TABLE IF EXISTS `municipalities`;

/* Table structure for table `users` */
CREATE TABLE `users` (
  `id` INT unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `document` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `municipality_id` SMALLINT unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_email` (`email`),
  UNIQUE KEY `IDX_phone` (`phone`),
  UNIQUE KEY `IDX_document` (`document`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

CREATE TABLE `authorities` (
      `id` INT unsigned NOT NULL AUTO_INCREMENT,
      `user_id` INT unsigned NOT NULL,
      `authority` varchar(50) NOT NULL,
       PRIMARY KEY (`id`),
       UNIQUE KEY `IDX_user_authority` (`user_id`,`authority`),
       CONSTRAINT `FK_authorities_users` foreign key(user_id) references users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

/* Table structure for table `regions` */
CREATE TABLE `regions` (
  `id` SMALLINT unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

/* Table structure for table `provinces` */
CREATE TABLE `provinces` (
  `id` SMALLINT unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `region_id` SMALLINT unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_name` (`name`),
  KEY `FK_provinces` (`region_id`),
  CONSTRAINT `FK_provinces` FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/* Table structure for table `municipalities` */
CREATE TABLE `municipalities` (
  `id` SMALLINT unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `province_id` SMALLINT unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_province_name` (`province_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8117 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;
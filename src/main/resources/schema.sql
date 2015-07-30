USE `master`;

-- Dropping tables
DROP TABLE IF EXISTS `posts`;
DROP TABLE IF EXISTS `post_comments`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `inner_comments`;
DROP TABLE IF EXISTS `authorities`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `provinces`;
DROP TABLE IF EXISTS `regions`;
DROP TABLE IF EXISTS `municipalities`;

-- Table structure for table `users`
CREATE TABLE `users` (
  `id` INT unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8_spanish_ci,
  `document` varchar(20) COLLATE utf8_spanish_ci,
  `password` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `municipality_id` SMALLINT unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_email` (`email`),
  UNIQUE KEY `IDX_phone` (`phone`),
  UNIQUE KEY `IDX_document` (`document`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- Table structure for table `authorities`
CREATE TABLE `authorities` (
      `id` INT unsigned NOT NULL AUTO_INCREMENT,
      `user_id` INT unsigned NOT NULL,
      `authority` varchar(50) NOT NULL,
       PRIMARY KEY (`id`),
       UNIQUE KEY `IDX_user_authority` (`user_id`,`authority`),
       CONSTRAINT `FK_authorities_users` foreign key(user_id) references users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- Table structure for table `regions`
CREATE TABLE `regions` (
  `id` SMALLINT unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- Table structure for table `provinces`
CREATE TABLE `provinces` (
  `id` SMALLINT unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `region_id` SMALLINT unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_name` (`name`),
  KEY `FK_provinces` (`region_id`),
  CONSTRAINT `FK_provinces` FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

-- Table structure for table `municipalities`
CREATE TABLE `municipalities` (
  `id` SMALLINT unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `province_id` SMALLINT unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_province_name` (`province_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8117 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

-- Table structure for table `posts`
CREATE TABLE `posts` (
  `id` INT unsigned NOT NULL AUTO_INCREMENT,
  `user_id` INT unsigned NOT NULL,
  `location_id` SMALLINT unsigned NOT NULL,
  `location_type` varchar(20) COLLATE utf8_spanish_ci,
  `title` varchar(100) COLLATE utf8_spanish_ci NOT NULL,
  `text` TEXT COLLATE utf8_spanish_ci NOT NULL,
  `comments_count` INT unsigned DEFAULT 0,
  `created_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- Define indexes for table `posts`
CREATE INDEX `location_id_location_type_idx` ON `posts` (`location_id`, `location_type`);
CREATE INDEX `user_id_idx` ON `posts` (`user_id`);

-- Table structure for table post_comments
CREATE TABLE IF NOT EXISTS post_comments
(
  post_id INT unsigned NOT NULL,
  comment_id INT unsigned NOT NULL,
  PRIMARY KEY(post_id, comment_id)
);

-- Table structure for table `comments`
CREATE TABLE `comments` (
  `id` INT unsigned NOT NULL AUTO_INCREMENT,
  `user_id` INT unsigned NOT NULL,
  `text` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `root_comment` boolean NOT NULL,
  `parent_id` INT unsigned,
  `comments_count` INT unsigned DEFAULT 0,
  `created_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- Table structure for table inner_comments
CREATE TABLE IF NOT EXISTS inner_comments
(
  comment_id INT unsigned NOT NULL,
  inner_comment_id INT unsigned NOT NULL,
  PRIMARY KEY(comment_id, inner_comment_id)
);
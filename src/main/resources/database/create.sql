CREATE TABLE IF NOT EXISTS `calendar` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `year` INTEGER NOT NULL,
    `month` INTEGER NOT NULL,
    `work_days` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `credentials` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `password` TEXT NOT NULL,
    `username` VARCHAR(200) NOT NULL UNIQUE,
    `role` TEXT NOT NULL,
    `enabled` TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `department` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(200) NOT NULL UNIQUE,
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `holidays` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `year` INTEGER NOT NULL,
    `month` INTEGER NOT NULL,
    `day` INTEGER NOT NULL,
    `description` TEXT,
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `projects` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `abbr` VARCHAR(200) NOT NULL UNIQUE,
    `name` TEXT NOT NULL,
    `full_name` TEXT NOT NULL,
    `company` TEXT NOT NULL,
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `workhours` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `type` TEXT NOT NULL,
    `hours_per_100days` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `employee` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `name` TEXT NOT NULL,
    `department_id` INTEGER NOT NULL,
    `work_hours_id` INTEGER NOT NULL,
    `credentials_id` INTEGER NOT NULL,
    `active` TINYINT(1) NOT NULL DEFAULT '1',
    FOREIGN KEY(`department_id`) REFERENCES `department`(`id`),
    FOREIGN KEY(`work_hours_id`) REFERENCES `workhours`(`id`),
    FOREIGN KEY(`credentials_id`) REFERENCES `credentials`(`id`),
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `log` (
   `id` int NOT NULL AUTO_INCREMENT,
   `employee_id` int NOT NULL,
   `project_id` int NOT NULL,
   `hours_worked_1000times` int NOT NULL,
   `service` text COLLATE utf8mb4_unicode_ci NOT NULL,
   `details` text COLLATE utf8mb4_unicode_ci,
   `month` int NOT NULL,
   `year` int NOT NULL,
   PRIMARY KEY (`id`),
   KEY `employee_id` (`employee_id`),
   KEY `project_id` (`project_id`),
   CONSTRAINT `log_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
   CONSTRAINT `log_ibfk_2` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

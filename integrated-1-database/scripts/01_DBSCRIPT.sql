DROP DATABASE IF EXISTS integrated;
CREATE DATABASE integrated CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE integrated;

SET GLOBAL time_zone = '+00:00';
SET time_zone = '+00:00';

CREATE TABLE tasks (
    taskId INT AUTO_INCREMENT PRIMARY KEY,
    taskTitle VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    taskDescription VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskAssignees VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskStatus ENUM('NO_STATUS', 'TO_DO', 'DOING', 'DONE') DEFAULT 'NO_STATUS',
    createdOn DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT CHK_taskTitle_not_empty CHECK (taskTitle <> ''),
	CONSTRAINT CHK_taskDescription_not_empty CHECK (taskDescription <> ''),
    CONSTRAINT CHK_taskAssignees_not_empty CHECK (taskAssignees <> '')
)ENGINE=InnoDB;

CREATE TABLE statuses (
    statusId INT AUTO_INCREMENT PRIMARY KEY,
    statusName VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
    statusDescription VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    statusColor VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '#CBD5E1',
    CONSTRAINT CHK_statusDescription_not_empty CHECK (TRIM(statusDescription) <> ''),
    CONSTRAINT CHK_statusDescription_length CHECK (CHAR_LENGTH(TRIM(statusDescription)) <= 200)
)ENGINE=InnoDB;

CREATE TABLE tasksV2 (
    taskId INT AUTO_INCREMENT PRIMARY KEY,
    taskTitle VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    taskDescription VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskAssignees VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    -- taskStatus VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'NO_STATUS',
    statusId INT DEFAULT 1,
    createdOn DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT CHK_taskTitle2_not_empty CHECK (taskTitle <> ''),
    CONSTRAINT CHK_taskDescription2_not_empty CHECK (taskDescription <> ''),
    CONSTRAINT CHK_taskAssignees2_not_empty CHECK (taskAssignees <> ''),
    CONSTRAINT FK_status FOREIGN KEY (statusId) REFERENCES statuses(statusId) ON DELETE RESTRICT
)ENGINE=InnoDB;

DELIMITER $$

CREATE TRIGGER before_insert_tasksV2
BEFORE INSERT ON tasksV2
FOR EACH ROW
BEGIN
    SET NEW.taskTitle = TRIM(NEW.taskTitle);
    SET NEW.taskDescription = TRIM(NEW.taskDescription);
    SET NEW.taskAssignees = TRIM(NEW.taskAssignees);
    IF NEW.statusId IS NULL THEN
        SET NEW.statusId = 1;
    END IF;
    IF NEW.createdOn IS NULL THEN
        SET NEW.createdOn = CURRENT_TIMESTAMP;
    END IF;
    IF NEW.updatedOn IS NULL THEN
        SET NEW.updatedOn = CURRENT_TIMESTAMP;
    END IF;
END$$

CREATE TRIGGER before_update_tasksV2
BEFORE UPDATE ON tasksV2
FOR EACH ROW
BEGIN
    SET NEW.taskTitle = TRIM(NEW.taskTitle);
    SET NEW.taskDescription = TRIM(NEW.taskDescription);
    SET NEW.taskAssignees = TRIM(NEW.taskAssignees);
    IF NEW.statusId IS NULL THEN
        SET NEW.statusId = 1;
    END IF;
    IF NEW.updatedOn IS NULL THEN
        SET NEW.updatedOn = CURRENT_TIMESTAMP;
    END IF;
END$$

CREATE TRIGGER before_insert_statuses
BEFORE INSERT ON statuses
FOR EACH ROW
BEGIN
    SET NEW.statusName = UPPER(REPLACE(TRIM(NEW.statusName), ' ', '_'));
	SET NEW.statusDescription = TRIM(NEW.statusDescription);
    SET NEW.statusColor = TRIM(NEW.statusColor);
    IF NEW.statusColor IS NULL THEN
        SET NEW.statusColor = '#CBD5E1';
    END IF;
END$$

CREATE TRIGGER before_update_statuses
BEFORE UPDATE ON statuses
FOR EACH ROW
BEGIN
	SET NEW.statusName = UPPER(REPLACE(TRIM(NEW.statusName), ' ', '_'));
	SET NEW.statusDescription = TRIM(NEW.statusDescription);
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER before_insert_tasks
BEFORE INSERT ON tasks
FOR EACH ROW
BEGIN
    SET NEW.taskTitle = TRIM(NEW.taskTitle);
    SET NEW.taskDescription = TRIM(NEW.taskDescription);
    SET NEW.taskAssignees = TRIM(NEW.taskAssignees);
    SET NEW.taskStatus = UPPER(REPLACE(TRIM(NEW.taskStatus), ' ', '_'));
    IF NEW.taskStatus IS NULL THEN
        SET NEW.taskStatus = 'NO_STATUS';
    END IF;
    IF NEW.createdOn IS NULL THEN
        SET NEW.createdOn = CURRENT_TIMESTAMP;
    END IF;
    IF NEW.updatedOn IS NULL THEN
        SET NEW.updatedOn = CURRENT_TIMESTAMP;
    END IF;
END$$

CREATE TRIGGER before_update_tasks
BEFORE UPDATE ON tasks
FOR EACH ROW
BEGIN
    SET NEW.taskTitle = TRIM(NEW.taskTitle);
    SET NEW.taskDescription = TRIM(NEW.taskDescription);
    SET NEW.taskAssignees = TRIM(NEW.taskAssignees);
    SET NEW.taskStatus = UPPER(REPLACE(TRIM(NEW.taskStatus), ' ', '_'));
    IF NEW.taskStatus IS NULL THEN
        SET NEW.taskStatus = 'NO_STATUS';
    END IF;
    IF NEW.updatedOn IS NULL THEN
        SET NEW.updatedOn = CURRENT_TIMESTAMP;
    END IF;
END$$

DELIMITER ;

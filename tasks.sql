DROP DATABASE IF EXISTS integrated;
CREATE DATABASE integrated CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE integrated;

SET GLOBAL time_zone = '+00:00';

CREATE TABLE tasks (
    taskId INT AUTO_INCREMENT PRIMARY KEY,
    taskTitle VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    taskDescription VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskAssignees VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskStatus ENUM('No Status', 'To Do', 'Doing', 'Done') DEFAULT 'No Status',
    createdOn DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT CHK_taskTitle_not_empty CHECK (taskTitle <> ''),
	CONSTRAINT CHK_taskDescription_not_empty CHECK (taskDescription <> ''),
    CONSTRAINT CHK_taskAssignees_not_empty CHECK (taskAssignees <> '')
);
DELIMITER $$

CREATE TRIGGER before_insert_tasks
BEFORE INSERT ON tasks
FOR EACH ROW
BEGIN
    SET NEW.taskTitle = TRIM(NEW.taskTitle);
    SET NEW.taskDescription = TRIM(NEW.taskDescription);
    SET NEW.taskAssignees = TRIM(NEW.taskAssignees);
    IF NEW.taskStatus IS NULL THEN
        SET NEW.taskStatus = 'No Status';
    END IF;
    IF NEW.createdOn IS NULL THEN
        SET NEW.createdOn = CURRENT_TIMESTAMP;
    END IF;
    IF NEW.updatedOn IS NULL THEN
        SET NEW.updatedOn = CURRENT_TIMESTAMP;
    END IF;
END$$

DELIMITER ;


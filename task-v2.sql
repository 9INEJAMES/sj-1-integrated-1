CREATE DATABASE integrated CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE integrated;

SET time_zone = '+07:00';

CREATE TABLE tasks (
    taskId INT AUTO_INCREMENT PRIMARY KEY,
    taskTitle VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    taskDescription VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskAssignees VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskStatus VARCHAR(20) DEFAULT 'No Status',
    createdOn DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT CHK_taskTitle_not_empty CHECK (taskTitle <> ''),
    CONSTRAINT CHK_taskDescription_not_empty CHECK (taskDescription <> ''),
    CONSTRAINT CHK_taskAssignees_not_empty CHECK (taskAssignees <> ''),
    CONSTRAINT FK_status FOREIGN KEY (taskStatus) REFERENCES task_statuses(statusId)
);

DELIMITER $$

CREATE TRIGGER before_insert_tasks
BEFORE INSERT ON tasks
FOR EACH ROW
BEGIN
    SET NEW.taskTitle = TRIM(NEW.taskTitle);
    SET NEW.taskDescription = TRIM(NEW.taskDescription);
    SET NEW.taskAssignees = TRIM(NEW.taskAssignees);
    IF NEW.createdOn IS NULL THEN
        SET NEW.createdOn = CURRENT_TIMESTAMP;
    END IF;
    IF NEW.updatedOn IS NULL THEN
        SET NEW.updatedOn = CURRENT_TIMESTAMP;
    END IF;
END$$

CREATE TRIGGER before_insert_task_statuses
BEFORE INSERT ON task_statuses
FOR EACH ROW
BEGIN
IF NEW.statusName IS NULL THEN
        SET NEW.statusName = 'No Status';
    END IF;
END$$

DELIMITER ;

CREATE TABLE task_statuses (
    statusName VARCHAR(20) NOT NULL
);

select * from tasks;
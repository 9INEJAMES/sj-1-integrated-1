DROP DATABASE IF EXISTS integratedV2;
CREATE DATABASE integratedV2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE integratedV2;

SET GLOBAL time_zone = '+00:00';

CREATE TABLE task_statuses (
    statusName VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci PRIMARY KEY,
    statusDescription VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    CONSTRAINT CHK_statusDescription_not_empty CHECK (TRIM(statusDescription) <> ''),
    CONSTRAINT CHK_statusDescription_length CHECK (CHAR_LENGTH(TRIM(statusDescription)) <= 200)
);

CREATE TABLE tasks (
    taskId INT AUTO_INCREMENT PRIMARY KEY,
    taskTitle VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    taskDescription VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskAssignees VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskStatus VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'No Status',
    createdOn DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT CHK_taskTitle_not_empty CHECK (taskTitle <> ''),
    CONSTRAINT CHK_taskDescription_not_empty CHECK (taskDescription <> ''),
    CONSTRAINT CHK_taskAssignees_not_empty CHECK (taskAssignees <> ''),
    CONSTRAINT FK_status FOREIGN KEY (taskStatus) REFERENCES task_statuses(statusName) ON DELETE CASCADE
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
    SET NEW.statusName = TRIM(NEW.statusName);
END$$

DELIMITER ;
INSERT INTO task_statuses (statusName,statusDescription) VALUES
('No Status','This item hasn''t been started'),
('To Do',null),
('Doing',null),
('Done','This item has been finished');
select * from task_statuses;
select * from tasks;
DROP DATABASE IF EXISTS integrated;
CREATE DATABASE integrated CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE integrated;

SET GLOBAL time_zone = '+00:00';
SET time_zone = '+00:00';

CREATE TABLE statuses (
    statusName VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci PRIMARY KEY,
    statusDescription VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    CONSTRAINT CHK_statusDescription_not_empty CHECK (TRIM(statusDescription) <> ''),
    CONSTRAINT CHK_statusDescription_length CHECK (CHAR_LENGTH(TRIM(statusDescription)) <= 200)
)ENGINE=InnoDB;

CREATE TABLE tasks (
    taskId INT AUTO_INCREMENT PRIMARY KEY,
    taskTitle VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    taskDescription VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskAssignees VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskStatus VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'NO_STATUS',
    createdOn DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT CHK_taskTitle_not_empty CHECK (taskTitle <> ''),
    CONSTRAINT CHK_taskDescription_not_empty CHECK (taskDescription <> ''),
    CONSTRAINT CHK_taskAssignees_not_empty CHECK (taskAssignees <> ''),
    CONSTRAINT FK_status FOREIGN KEY (taskStatus) REFERENCES statuses(statusName) ON DELETE CASCADE
)ENGINE=InnoDB;

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

DELIMITER ;

DELIMITER $$

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

CREATE TRIGGER before_insert_statuses
BEFORE INSERT ON statuses
FOR EACH ROW
BEGIN
    SET NEW.statusName = UPPER(REPLACE(TRIM(NEW.statusName), ' ', '_'));
	SET NEW.statusDescription = TRIM(NEW.statusDescription);
END$$

CREATE TRIGGER before_update_statuses
BEFORE UPDATE ON statuses
FOR EACH ROW
BEGIN
	SET NEW.statusName = UPPER(REPLACE(TRIM(NEW.statusName), ' ', '_'));
	SET NEW.statusDescription = TRIM(NEW.statusDescription);
END$$

DELIMITER ;

USE integrated;

INSERT INTO statuses (statusName,statusDescription) VALUES
('NO_STATUS','This item hasn''t been started'),
('TO_DO',null),
('DOING',null),
('DONE','This item has been finished');

INSERT INTO tasks (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn) 
VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0', 'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5', 'Assignees1Assignees2Assignees3', 'NO_STATUS','2024-04-22 09:00:00+00:00','2024-04-22 09:00:00+00:00'),
('Repository', null, null, 'TO_DO', '2024-04-22 09:05:00+00:00', '2024-04-22 14:00:00+00:00'),
('ดาต้าเบส', 'ສ້າງຖານຂໍ້ມູນ', 'あなた、彼、彼女 (私ではありません)', 'DOING', '2024-04-22 09:10:00+00:00', '2024-04-25 00:00:00+00:00'),
('_Infrastructure_', '_Setup containers_', 'ไก่งวง กับ เพนกวิน', 'DONE','2024-04-22 09:15:00+00:00','2024-04-22 10:00:00+00:00');

select * from statuses;
select * from tasks;

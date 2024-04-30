DROP DATABASE IF EXISTS integrated;
CREATE DATABASE IF NOT EXISTS integrated CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE integrated;

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
DELIMITER $$

CREATE TRIGGER before_insert_tasks
BEFORE INSERT ON tasks
FOR EACH ROW
BEGIN
    SET NEW.taskTitle = TRIM(NEW.taskTitle);
    SET NEW.taskDescription = TRIM(NEW.taskDescription);
    SET NEW.taskAssignees = UPPER(REPLACE(TRIM(NEW.taskAssignees), ' ', '_'));
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

USE integrated;

INSERT INTO tasks (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn) 
VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0', 'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5', 'Assignees1Assignees2Assignees3', 'NO_STATUS','2024-04-22 09:00:00+00:00','2024-04-22 09:00:00+00:00'),
('Repository', null, null, 'TO_DO', '2024-04-22 09:05:00+00:00', '2024-04-22 14:00:00+00:00'),
('ดาต้าเบส', 'ສ້າງຖານຂໍ້ມູນ', 'あなた、彼、彼女 (私ではありません)', 'DOING', '2024-04-22 09:10:00+00:00', '2024-04-25 00:00:00+00:00'),
('_Infrastructure_', '_Setup containers_', 'ไก่งวง กับ เพนกวิน', 'DONE','2024-04-22 09:15:00+00:00','2024-04-22 10:00:00+00:00');





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

CREATE TABLE limit_task (
	limitId INT AUTO_INCREMENT PRIMARY KEY,
	isLimit BOOLEAN DEFAULT FALSE,
    limitMaximumTask INT NOT NULL DEFAULT 10
) ENGINE=InnoDB;

USE integrated;

INSERT INTO limit_task (limitMaximumTask) VALUES (10);

DELIMITER $$

CREATE TRIGGER limit_task_before_insert
BEFORE INSERT ON limit_task
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot insert new rows into limit_task';
END;

CREATE TRIGGER limit_task_before_delete
BEFORE DELETE ON limit_task
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot delete rows from limit_task';
END;

CREATE TRIGGER limit_task_before_update
BEFORE UPDATE ON limit_task
FOR EACH ROW
BEGIN
    SET NEW.limitId = 1;
    IF NEW.isLimit IS NULL THEN
        SET NEW.isLimit = OLD.isLimit;
    END IF;
    IF NEW.limitMaximumTask IS NULL THEN
        SET NEW.limitMaximumTask = OLD.limitMaximumTask;
    END IF;
END$$

DELIMITER ;

CREATE TABLE statuses (
    statusId INT AUTO_INCREMENT PRIMARY KEY,
    statusName VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
    statusDescription VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    statusColor VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '#cbd5e1',
    limitId INT DEFAULT 1,
    CONSTRAINT FK_limit FOREIGN KEY (limitId) REFERENCES limit_task(limitId) ON DELETE RESTRICT,
    CONSTRAINT CHK_statusDescription_not_empty CHECK (TRIM(statusDescription) <> ''),
    CONSTRAINT CHK_statusDescription_length CHECK (CHAR_LENGTH(TRIM(statusDescription)) <= 200)
)ENGINE=InnoDB;

DELIMITER $$

CREATE TRIGGER before_insert_statuses
BEFORE INSERT ON statuses
FOR EACH ROW
BEGIN
    SET NEW.limitId = 1;
    SET NEW.statusName = TRIM(NEW.statusName);
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
	IF NEW.statusId = 1 OR NEW.statusId = 4 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot edit default status';
	END IF;
    SET NEW.limitId = 1;
	SET NEW.statusName = TRIM(NEW.statusName);
	SET NEW.statusDescription = TRIM(NEW.statusDescription);
END$$

CREATE TRIGGER before_delete_statuses
BEFORE DELETE ON statuses
FOR EACH ROW
BEGIN
	IF OLD.statusId = 1 OR OLD.statusId = 4 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot delet default status';
	END IF;
END$$

DELIMITER ;

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
	DECLARE isLimitValue INT;
	DECLARE limit_value INT;
    DECLARE tasks_count INT;
	IF NEW.statusId IS NULL THEN
        SET NEW.statusId = 1;
    END IF;
    
    IF NEW.statusId != 1 AND NEW.statusId != 4 THEN
		SELECT isLimit INTO isLimitValue FROM limit_task;
        IF isLimitValue = 1 THEN
        SELECT COUNT(*) INTO tasks_count FROM tasksV2 WHERE statusId = NEW.statusId;
        SELECT limitMaximumTask INTO limit_value FROM limit_task;
			IF tasks_count >= limit_value THEN
				SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot add task more than limit';
            END IF;
		END IF;
    END IF;
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

DELIMITER ;

USE integrated;

INSERT INTO tasks (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn) VALUES 
('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0', 'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5', 'Assignees1Assignees2Assignees3', 'NO_STATUS','2024-04-22 09:00:00+00:00','2024-04-22 09:00:00+00:00'),
('Repository', null, null, 'TO_DO', '2024-04-22 09:05:00+00:00', '2024-04-22 14:00:00+00:00'),
('ดาต้าเบส', 'ສ້າງຖານຂໍ້ມູນ', 'あなた、彼、彼女 (私ではありません)', 'DOING', '2024-04-22 09:10:00+00:00', '2024-04-25 00:00:00+00:00'),
('_Infrastructure_', '_Setup containers_', 'ไก่งวง กับ เพนกวิน', 'DONE','2024-04-22 09:15:00+00:00','2024-04-22 10:00:00+00:00');

INSERT INTO statuses (statusName,statusDescription,statusColor) VALUES
('No Status','The default status','#cbd5e1'),
('To Do','The task is included in the project','#fcd34d'),
('In Progress','The task is being worked on','#fcd34d'),
('Reviewing','The task is being reviewed','#fcd34d'),
('Testing','The task is being tested','#fcd34d'),
('Waiting','The task is waiting for a resource','#fcd34d'),
('Done','Finished','#10b981');

INSERT INTO tasksV2 (taskTitle, statusId, createdOn, updatedOn) VALUES 
('NS01', 1,'2024-05-14 09:00:00+00:00','2024-05-14 09:00:00+00:00'),
('TD01', 2,'2024-05-14 09:00:00+00:00','2024-05-14 09:00:00+00:00'),
('IP01', 3,'2024-05-14 09:20:00+00:00','2024-05-14 09:20:00+00:00'),
('TD02', 2,'2024-05-14 09:30:00+00:00','2024-05-14 09:30:00+00:00'),
('DO01', 7,'2024-05-14 09:40:00+00:00','2024-05-14 09:40:00+00:00'),
('IP02', 3,'2024-05-14 09:50:00+00:00','2024-05-14 09:50:00+00:00');

select * from tasksV2;
select * from statuses;
select * from limit_task;
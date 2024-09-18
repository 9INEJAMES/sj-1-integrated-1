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

INSERT INTO tasks (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn) VALUES 
('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0', 'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5', 'Assignees1Assignees2Assignees3', 'NO_STATUS','2024-04-22 09:00:00+00:00','2024-04-22 09:00:00+00:00'),
('Repository', null, null, 'TO_DO', '2024-04-22 09:05:00+00:00', '2024-04-22 14:00:00+00:00'),
('ดาต้าเบส', 'ສ້າງຖານຂໍ້ມູນ', 'あなた、彼、彼女 (私ではありません)', 'DOING', '2024-04-22 09:10:00+00:00', '2024-04-25 00:00:00+00:00'),
('_Infrastructure_', '_Setup containers_', 'ไก่งวง กับ เพนกวิน', 'DONE','2024-04-22 09:15:00+00:00','2024-04-22 10:00:00+00:00');

CREATE TABLE boards (
    boardId VARCHAR(10) PRIMARY KEY,
    boardName VARCHAR(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    ownerId VARCHAR(36),
    isLimit BOOLEAN DEFAULT FALSE,
    limitMaximumTask INT NOT NULL DEFAULT 10,
    isPublic BOOLEAN DEFAULT FALSE,
    createdOn DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT CHK_boardName_not_empty CHECK (boardName <> '')
) ENGINE=InnoDB;

DELIMITER $$

CREATE TRIGGER trg_before_insert_boards
BEFORE INSERT ON boards
FOR EACH ROW
BEGIN
     IF NEW.isLimit IS NULL THEN
        SET NEW.isLimit = false;
    END IF;
    IF NEW.limitMaximumTask IS NULL THEN
        SET NEW.limitMaximumTask = 10;
    END IF;
    IF NEW.isPublic IS NULL THEN
        SET NEW.isPublic = false;
    END IF;
    IF NEW.createdOn IS NULL THEN
        SET NEW.createdOn = CURRENT_TIMESTAMP;
    END IF;
    IF NEW.updatedOn IS NULL THEN
        SET NEW.updatedOn = CURRENT_TIMESTAMP;
    END IF;
END$$

CREATE TRIGGER limit_task_before_update
BEFORE UPDATE ON boards
FOR EACH ROW
BEGIN
    IF NEW.isLimit IS NULL THEN
        SET NEW.isLimit = OLD.isLimit;
    END IF;
    IF NEW.limitMaximumTask IS NULL THEN
        SET NEW.limitMaximumTask = OLD.limitMaximumTask;
    END IF;
    IF NEW.isPublic IS NULL THEN
        SET NEW.isPublic = OLD.isPublic;
    END IF;
	SET NEW.createdOn = OLD.isPublic;
	IF NEW.updatedOn IS NULL THEN
        SET NEW.updatedOn = CURRENT_TIMESTAMP;
    END IF;
END$$

DELIMITER ;

CREATE TABLE statuses (
    statusId INT AUTO_INCREMENT UNIQUE,
    boardId VARCHAR(10) NOT NULL,
    statusName VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    statusDescription VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    statusColor VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '#cbd5e1',
    PRIMARY KEY (statusId, boardId),
    CONSTRAINT FK_boardId_statuses FOREIGN KEY (boardId) REFERENCES boards(boardId) ON DELETE CASCADE,
    CONSTRAINT CHK_statusDescription_not_empty CHECK (TRIM(statusDescription) <> ''),
    CONSTRAINT CHK_statusDescription_length CHECK (CHAR_LENGTH(TRIM(statusDescription)) <= 200)
) ENGINE=InnoDB;

DELIMITER $$

CREATE TRIGGER before_insert_statuses
BEFORE INSERT ON statuses
FOR EACH ROW
BEGIN
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
	IF NEW.statusName = 'No Status' OR NEW.statusName = 'Done' THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot edit default status';
	END IF;
	SET NEW.statusName = TRIM(NEW.statusName);
	SET NEW.statusDescription = TRIM(NEW.statusDescription);
END$$

CREATE TRIGGER before_delete_statuses
BEFORE DELETE ON statuses
FOR EACH ROW
BEGIN
	IF OLD.statusName = 'No Status' OR OLD.statusName = 'Done' THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot delete default status';
	END IF;
END$$

DELIMITER ;

CREATE TABLE tasksV2 (
    taskId INT AUTO_INCREMENT,
    boardId VARCHAR(10) NOT NULL,
    taskTitle VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    taskDescription VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskAssignees VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    statusId INT DEFAULT 1,
    createdOn DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (taskId, boardId),
    CONSTRAINT CHK_taskTitle2_not_empty CHECK (taskTitle <> ''),
    CONSTRAINT CHK_taskDescription2_not_empty CHECK (taskDescription <> ''),
    CONSTRAINT CHK_taskAssignees2_not_empty CHECK (taskAssignees <> ''),
    CONSTRAINT FK_status FOREIGN KEY (statusId) REFERENCES statuses(statusId) ON DELETE RESTRICT,
    CONSTRAINT FK_boardId_tasksV2 FOREIGN KEY (boardId) REFERENCES boards(boardId) ON DELETE CASCADE
) ENGINE=InnoDB;

DELIMITER $$

CREATE TRIGGER before_insert_tasksV2
BEFORE INSERT ON tasksV2
FOR EACH ROW
BEGIN
    DECLARE isLimitValue INT;
    DECLARE limit_value INT;
    DECLARE tasks_count INT;
    DECLARE current_status VARCHAR(50);
    DECLARE status_exists INT;

    -- Check if statusId exists for the boardId or in the default board 'kanbanbase'
    SELECT COUNT(*) INTO status_exists
    FROM statuses
    WHERE statusId = NEW.statusId AND (boardId = NEW.boardId OR boardId = 'kanbanbase');
    
    IF status_exists = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'StatusId does not exist for the specified board or the default board';
    END IF;

    -- Set default values for NULL fields
    IF NEW.statusId IS NULL THEN
        SET NEW.statusId = 1;
    END IF;
    
    SELECT statusName INTO current_status FROM statuses WHERE statusId = NEW.statusId;

    IF current_status != 'No Status' AND current_status != 'Done' THEN
        SELECT isLimit INTO isLimitValue FROM boards WHERE boardId = NEW.boardId;
        IF isLimitValue = 1 THEN
            SELECT COUNT(*) INTO tasks_count FROM tasksV2 WHERE statusId = NEW.statusId;
            SELECT limitMaximumTask INTO limit_value FROM boards WHERE boardId = NEW.boardId;
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
    DECLARE current_status VARCHAR(50);
    DECLARE isLimitValue INT;
    DECLARE limit_value INT;
    DECLARE tasks_count INT;
    DECLARE status_exists INT;

    -- Check if statusId exists for the boardId or in the default board 'kanbanbase'
    SELECT COUNT(*) INTO status_exists
    FROM statuses
    WHERE statusId = NEW.statusId AND (boardId = NEW.boardId OR boardId = 'kanbanbase');

    IF status_exists = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'StatusId does not exist for the specified board or the default board';
    END IF;

    SET NEW.taskTitle = TRIM(NEW.taskTitle);
    SET NEW.taskDescription = TRIM(NEW.taskDescription);
    SET NEW.taskAssignees = TRIM(NEW.taskAssignees);

    IF NEW.statusId IS NULL THEN
        SET NEW.statusId = 1;
    END IF;

    SELECT statusName INTO current_status FROM statuses WHERE statusId = NEW.statusId;

    IF current_status != 'No Status' AND current_status != 'Done' THEN
        SELECT isLimit INTO isLimitValue FROM boards WHERE boardId = NEW.boardId;
        IF isLimitValue = 1 THEN
            SELECT COUNT(*) INTO tasks_count FROM tasksV2 WHERE statusId = NEW.statusId;
            SELECT limitMaximumTask INTO limit_value FROM boards WHERE boardId = NEW.boardId;
            IF tasks_count >= limit_value+1 THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot add task more than limit';
            END IF;
        END IF;
    END IF;

    IF NEW.updatedOn IS NULL THEN
        SET NEW.updatedOn = CURRENT_TIMESTAMP;
    END IF;
END$$

DELIMITER ;


USE integrated;

INSERT INTO boards (boardId, boardName, ownerId) VALUES 
('kanbanbase', 'Default', null);

INSERT INTO statuses (boardId, statusName,statusDescription,statusColor) VALUES
('kanbanbase','No Status','The default status','#cbd5e1'),
('kanbanbase','To Do','The task is included in the project','#99ddff'),
('kanbanbase','Doing','The task is being worked on','#fabc3f'),
('kanbanbase','Done','Finished','#10b981');

SELECT * FROM boards;
SELECT * FROM tasksV2;
SELECT * FROM statuses;
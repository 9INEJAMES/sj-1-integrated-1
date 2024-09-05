DROP DATABASE IF EXISTS integrated;
CREATE DATABASE integrated CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE integrated;

SET GLOBAL time_zone = '+00:00';
SET time_zone = '+00:00';

CREATE TABLE boards (
    boardId VARCHAR(10) PRIMARY KEY,
    boardName VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    ownerId VARCHAR(36),
    isLimit BOOLEAN DEFAULT FALSE,
    limitMaximumTask INT NOT NULL DEFAULT 10,
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
END$$

DELIMITER ;

CREATE TABLE statuses (
    statusId INT AUTO_INCREMENT,
    boardId VARCHAR(10) NOT NULL,
    statusName VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
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

	IF NEW.statusId IS NULL THEN
        SET NEW.statusId = 1;
    END IF;
    SELECT statusName INTO current_status FROM statuses WHERE statusId =NEW.statusId;
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
    SET NEW.taskTitle = TRIM(NEW.taskTitle);
    SET NEW.taskDescription = TRIM(NEW.taskDescription);
    SET NEW.taskAssignees = TRIM(NEW.taskAssignees);
    IF NEW.statusId IS NULL THEN
        SET NEW.statusId = 1;
    END IF;

    SELECT statusName INTO current_status FROM statuses WHERE statusId =NEW.statusId;
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
('kanbanbase','Done','Finished','#10b981');

SELECT * FROM boards;
SELECT * FROM tasksV2;
SELECT * FROM statuses;
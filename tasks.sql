CREATE DATABASE integrated CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE integrated;

SET time_zone = '+00:00';

CREATE TABLE tasks (
    taskId INT AUTO_INCREMENT PRIMARY KEY,
    taskTitle VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    taskDescription VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskAssignees VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    taskStatus ENUM('No Status', 'To Do', 'Doing', 'Done') NOT NULL DEFAULT 'No Status',
    createdOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT CHK_taskTitle_not_empty CHECK (taskTitle <> ''),
    CONSTRAINT CHK_taskAssignees_not_empty CHECK (taskAssignees <> '')
);


INSERT INTO tasks (taskTitle, taskDescription, taskAssignees, taskStatus) 
VALUES ('Design UI Mockups', 'Create mockups for the new project', 'John Doe', 'To Do');

INSERT INTO tasks (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn) 
VALUES ('Implement Backend API', 'Develop RESTful API endpoints', 'Jane Smith', 'Doing', NOW(), NOW());

INSERT INTO tasks (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn) 
VALUES ('Write Documentation', 'Document the project requirements and architecture', 'John Doe, Jane Smith', 'To Do', NOW(), NOW());

INSERT INTO tasks (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn) 
VALUES ('Perform Testing', 'Conduct unit and integration tests', 'Alice Johnson', 'Doing', NOW(), NOW());

INSERT INTO tasks (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn) 
VALUES ('Deploy to Production', 'Release the application to production environment', 'Bob Williams', 'Done', NOW(), NOW());

select * from tasks;
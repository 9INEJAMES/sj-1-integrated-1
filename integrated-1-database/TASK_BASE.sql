USE integrated;

INSERT INTO tasks (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn) 
VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0', 'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5', 'Assignees1Assignees2Assignees3', 'NO_STATUS','2024-04-22 09:00:00+00:00','2024-04-22 09:00:00+00:00'),
('Repository', null, null, 'TO_DO', '2024-04-22 09:05:00+00:00', '2024-04-22 14:00:00+00:00'),
('ดาต้าเบส', 'ສ້າງຖານຂໍ້ມູນ', 'あなた、彼、彼女 (私ではありません)', 'DOING', '2024-04-22 09:10:00+00:00', '2024-04-25 00:00:00+00:00'),
('_Infrastructure_', '_Setup containers_', 'ไก่งวง กับ เพนกวิน', 'DONE','2024-04-22 09:15:00+00:00','2024-04-22 10:00:00+00:00');

INSERT INTO statuses (statusName,statusDescription,statusColor) VALUES
('NO_STATUS','This item hasn''t been started','#CBD5E1'),
('TO_DO',null,'#FCD34D'),
('DOING',null,'#7DD3FC'),
('DONE','This item has been finished','#10B981');

INSERT INTO tasksV2 (taskTitle, taskDescription, taskAssignees, statusId, createdOn, updatedOn) 
VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0', 'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5', 'Assignees1Assignees2Assignees3', 1,'2024-04-22 09:00:00+00:00','2024-04-22 09:00:00+00:00'),
('Repository', null, null, 2, '2024-04-22 09:05:00+00:00', '2024-04-22 14:00:00+00:00'),
('ดาต้าเบส', 'ສ້າງຖານຂໍ້ມູນ', 'あなた、彼、彼女 (私ではありません)', 3, '2024-04-22 09:10:00+00:00', '2024-04-25 00:00:00+00:00'),
('_Infrastructure_', '_Setup containers_', 'ไก่งวง กับ เพนกวิน', 4,'2024-04-22 09:15:00+00:00','2024-04-22 10:00:00+00:00');

select * from tasks;
select * from statuses;
select * from tasksV2;
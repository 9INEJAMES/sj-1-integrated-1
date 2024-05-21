USE integrated;

INSERT INTO tasks (taskTitle, taskStatus, createdOn, updatedOn) 
VALUES ('NS01', 'NO_STATUS','2024-05-14 09:00:00+00:00','2024-05-14 09:00:00+00:00'),
('TD01', 'TO_DO','2024-05-14 09:00:00+00:00','2024-05-14 09:00:00+00:00'),
('IP01', 'IN_PROGRESS','2024-05-14 09:20:00+00:00','2024-05-14 09:20:00+00:00'),
('TD02', 'TO_DO','2024-05-14 09:30:00+00:00','2024-05-14 09:30:00+00:00'),
('DO01', 'DONE','2024-05-14 09:40:00+00:00','2024-05-14 09:40:00+00:00'),
('IP02', 'IN_PROGRESS','2024-05-14 09:50:00+00:00','2024-05-14 09:50:00+00:00')

INSERT INTO tasksV2 (taskTitle, statusId, createdOn, updatedOn) 
VALUES ('NS01', 1,'2024-05-14 09:00:00+00:00','2024-05-14 09:00:00+00:00'),
('TD01', 2,'2024-05-14 09:00:00+00:00','2024-05-14 09:00:00+00:00'),
('IP01', 3,'2024-05-14 09:20:00+00:00','2024-05-14 09:20:00+00:00'),
('TD02', 2,'2024-05-14 09:30:00+00:00','2024-05-14 09:30:00+00:00'),
('DO01', 7,'2024-05-14 09:40:00+00:00','2024-05-14 09:40:00+00:00'),
('IP02', 3,'2024-05-14 09:50:00+00:00','2024-05-14 09:50:00+00:00')


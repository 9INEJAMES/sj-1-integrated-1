package int221.integrated1backend.services;

import int221.integrated1backend.dtos.TaskDTO;
import int221.integrated1backend.dtos.TaskWithIdDTO;
import int221.integrated1backend.entities.Task;
import int221.integrated1backend.repositories.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;
    @Autowired

    private ModelMapper modelMapper;


    public List<Task> getAllTask() {
        return repository.findAll();
    }

    public Task findByID(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task id " + id + " does not exists !!!"));
    }

    private String isStringNull(String string) {
        return string == null ? null : !string.trim().isEmpty() ? string.trim() : null;
    }
    private String isStringNull(String string,String oldString) {
        return string == null ? oldString : !string.trim().isEmpty() ? string.trim() : oldString;
    }

    @Transactional
    public TaskWithIdDTO createNewTask(TaskDTO taskDTO) {
        Task tmp = modelMapper.map(taskDTO, Task.class);
        tmp.setTitle(isStringNull(tmp.getTitle()));
        tmp.setDescription(isStringNull(tmp.getDescription()));
        tmp.setAssignees(isStringNull(tmp.getAssignees()));
        tmp.setStatus(isStringNull(tmp.getStatus()) == null ? "NO_STATUS" : isStringNull(tmp.getStatus()));
        Task newTask = repository.save(tmp);
        return modelMapper.map(newTask, TaskWithIdDTO.class);
    }

    @Transactional
    public TaskWithIdDTO removeTask(Integer taskId) {
        Task task = repository.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        repository.delete(task);
        return modelMapper.map(task, TaskWithIdDTO.class);
    }

    @Transactional
    public TaskWithIdDTO updateTask(Integer taskId, TaskDTO taskDTO) {
        Task task = modelMapper.map(taskDTO, Task.class);
        task.setId(taskId);

        Task existingTask = findByID(taskId);
        existingTask.setTitle(isStringNull(task.getTitle(), existingTask.getTitle()));
        existingTask.setDescription(isStringNull(task.getDescription()));
        existingTask.setAssignees(isStringNull(task.getAssignees()));
        existingTask.setStatus(isStringNull(task.getStatus(), existingTask.getStatus()));
//        existingTask.setUpdatedOn(new Date());
        Task result = repository.save(existingTask);
        return modelMapper.map(result, TaskWithIdDTO.class);
    }

}

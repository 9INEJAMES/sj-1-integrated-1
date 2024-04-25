package int221.integrated1backend.services;

import int221.integrated1backend.dtos.TaskDTO;
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
        return string != null ? string.trim() : null;
    }

    @Transactional
    public Task createNewTask(TaskDTO taskDTO) {
        Task tmp = modelMapper.map(taskDTO, Task.class);
        tmp.setTitle(isStringNull(tmp.getTitle()));
        tmp.setDescription(isStringNull(tmp.getDescription()));
        tmp.setAssignees(isStringNull(tmp.getAssignees()));
        tmp.setStatus(tmp.getStatus()!=null? tmp.getStatus() : "No Status");
//        tmp.setCreatedOn();
//        tmp.setUpdatedOn();
        Task newTask = repository.save(tmp);
//        newTask.setCreatedOn();
//        newTask.setUpdatedOn();
        return newTask;
    }

    @Transactional
    public void removeTask(Integer taskId) {
        Task task = repository.findById(taskId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Task Id " + taskId + " DOES NOT EXIST !!!"));
        repository.delete(task);
    }

    @Transactional
    public Task updateTask(Integer taskId, TaskDTO taskDTO) {
        Task task = modelMapper.map(taskDTO, Task.class);
        task.setId(taskId);

        Task existingTask = findByID(taskId);
        if (task.getTitle() != null) existingTask.setTitle(task.getTitle().trim());
        existingTask.setDescription(isStringNull(task.getDescription()));
        existingTask.setAssignees(isStringNull(task.getAssignees()));
        existingTask.setStatus(task.getStatus()!=null? task.getStatus() : "No Status");
        existingTask.setUpdatedOn(new Date());
        Task result = repository.save(existingTask);
        return result;
    }

}

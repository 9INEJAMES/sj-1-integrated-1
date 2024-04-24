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
import java.time.format.DateTimeFormatter;
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
                        "Task ID '" + id + "' NOT FOUND !!!"));
    }

    @Transactional
    public Task createNewTask(TaskDTO taskDTO) {
        Task tmp = modelMapper.map(taskDTO, Task.class);
        tmp.setTitle(tmp.getTitle().trim());
        tmp.setDescription(tmp.getDescription().trim());
        tmp.setAssignees(tmp.getAssignees().trim());
        Task newTask = repository.save(tmp);
        newTask.setCreatedOn();
        newTask.setUpdatedOn();
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

        Task existingTask = repository.findById(taskId).orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND,
                        "Task Id " + taskId + " DOES NOT EXIST !!!"));
        if (task.getTitle() == null) task.setTitle(existingTask.getTitle());
        if (task.getDescription() == null) task.setDescription(existingTask.getDescription());
        if (task.getAssignees() == null) task.setAssignees(existingTask.getAssignees());
        if (task.getStatus() == null) task.setStatus(existingTask.getStatus());

        task.setTitle(task.getTitle().trim());
        task.setDescription(task.getDescription().trim());
        task.setAssignees(task.getAssignees().trim());
        task.setCreatedOn(Timestamp.valueOf(LocalDateTime.parse(existingTask.getCreatedOn(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))));
        task.setUpdatedOn();//ถ้าไม่ใส่ตรงนี้จะ error json
        Task result = repository.save(task);
        return result;
    }

}

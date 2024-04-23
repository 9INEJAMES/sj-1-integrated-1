package int221.integrated1backend.services;

import int221.integrated1backend.dtos.TaskDTO;
import int221.integrated1backend.entities.Task;
import int221.integrated1backend.repositories.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
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
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task ID '"+ id + "' NOT FOUND !!!"));
    }

    @Transactional
    public Task createNewTask(TaskDTO taskDTO) {
        Task tmp = modelMapper.map(taskDTO,Task.class);
        tmp.setTaskAssignees(tmp.getTaskAssignees().trim());
        tmp.setCreatedOn(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        tmp.setUpdatedOn(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return repository.save(tmp);
    }
}

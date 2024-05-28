package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.TaskInputDTO;
import int221.integrated1backend.dtos.TaskOutputDTO;
import int221.integrated1backend.entities.Task;
import int221.integrated1backend.services.ListMapper;
import int221.integrated1backend.services.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tasks")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
public class TaskControllerV1 {
    @Autowired
    private TaskService service;

    @GetMapping("")
    public ResponseEntity<Object> getAllTask() {
        List<Task> taskList = service.getAllTask();
        return ResponseEntity.ok(taskList);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer taskId) {
        Task task = service.findByID(taskId);
        return ResponseEntity.ok(task);
    }

    @PostMapping("")
    public ResponseEntity<Object> addNewTask(@RequestBody TaskInputDTO taskDTO) {
        Task task = service.createNewTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Object> updateTask(@PathVariable Integer taskId, @RequestBody TaskInputDTO taskDTO) {
        Task task = service.updateTask(taskId, taskDTO);
        return ResponseEntity.ok(task);
    }
    
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Object> deleteTask(@PathVariable Integer taskId) {
        return ResponseEntity.ok(service.removeTask(taskId));
    }
}



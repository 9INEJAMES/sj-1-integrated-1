package int221.integrated1backend.controllers;


import int221.integrated1backend.dtos.TaskDTO;
import int221.integrated1backend.entities.Task;
import int221.integrated1backend.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itb-kk")
public class TaskController {
    @Autowired
    private TaskService service;
    @GetMapping("/v1/tasks")
    public ResponseEntity<Object> getAllSupplier() {
        List<Task> taskList = service.getAllTask();
        return ResponseEntity.ok(taskList);
    }

    @GetMapping("/v1/tasks/{taskId}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer taskId){
        Task task = service.findByID(taskId);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/v1/tasks")
    public ResponseEntity<Object> addNewTask(@RequestBody TaskDTO taskDTO){
        Task task = service.createNewTask(taskDTO);
        return  ResponseEntity.ok(task);
    }

    @PatchMapping("/v1/tasks/{taskId}")
    public ResponseEntity<Object> updateTask(@PathVariable Integer taskId,@RequestBody TaskDTO taskDTO){
        Task task = service.updateTask(taskId,taskDTO);
        return ResponseEntity.ok(task);
    }
}



package int221.integrated1backend.controllers;


import int221.integrated1backend.dtos.TaskDTO;
import int221.integrated1backend.entities.Task;
import int221.integrated1backend.services.ListMapper;
import int221.integrated1backend.services.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {
    @Autowired
    private TaskService service;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    @GetMapping("")
    public ResponseEntity<Object> getAllSupplier() {
        List<Task> taskList = service.getAllTask();
        return ResponseEntity.ok(taskList);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer taskId){
        Task task = service.findByID(taskId);
        return ResponseEntity.ok(task);
    }

    @PostMapping("")
    public ResponseEntity<Object> addNewTask(@RequestBody TaskDTO taskDTO){
        Task task = service.createNewTask(taskDTO);
        return  ResponseEntity.ok(task);
    }
}



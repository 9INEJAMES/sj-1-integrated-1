package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.TaskDTO;
import int221.integrated1backend.dtos.TaskWithIdDTO;
import int221.integrated1backend.entities.Task;
import int221.integrated1backend.services.ListMapper;
import int221.integrated1backend.services.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskControllerV1 {
    @Autowired
    private TaskService service;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @GetMapping("/tasks")
    public ResponseEntity<Object> getAllSupplier() {
        List<Task> taskList = service.getAllTask();
        List<TaskWithIdDTO> taskWithIdDTOList = listMapper.mapList(taskList, TaskWithIdDTO.class,modelMapper);
        return ResponseEntity.ok(taskWithIdDTOList);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer taskId){
        Task task = service.findByID(taskId);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/tasks")
    public ResponseEntity<Object> addNewTask(@RequestBody TaskDTO taskDTO){
        Task task = service.createNewTask(taskDTO);
        TaskWithIdDTO taskWithIdDTO = modelMapper.map(task,TaskWithIdDTO.class);
        return  ResponseEntity.ok(taskWithIdDTO);
    }

    @PatchMapping("/tasks/{taskId}")
    public ResponseEntity<Object> updateTask(@PathVariable Integer taskId,@RequestBody TaskDTO taskDTO){
        Task task = service.updateTask(taskId,taskDTO);
        return ResponseEntity.ok(task);
    }
}



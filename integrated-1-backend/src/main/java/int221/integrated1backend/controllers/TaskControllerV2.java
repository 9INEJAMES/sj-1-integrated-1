package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.TaskInputDTO;
import int221.integrated1backend.dtos.TaskOutputDTO;
import int221.integrated1backend.entities.Status;
import int221.integrated1backend.entities.Task;
import int221.integrated1backend.entities.TaskV2;
import int221.integrated1backend.services.ListMapper;
import int221.integrated1backend.services.StatusService;
import int221.integrated1backend.services.TaskService;
import int221.integrated1backend.services.TaskV2Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v2/tasks")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
public class TaskControllerV2 {
    @Autowired
    private TaskV2Service service;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    @GetMapping("")
    public ResponseEntity<Object> getAllTask(
            @RequestParam(defaultValue = "") String[] statuses,
            @RequestParam(defaultValue = "") String[] sortBy,
            @RequestParam(defaultValue = "ASC") String[] sortDirection) {
        if (statuses.length > 0) return ResponseEntity.ok(service.getAllTask(statuses, sortBy, sortDirection));
        else {
            List<TaskV2> taskList = service.getAllTask();
            List<TaskOutputDTO> taskDTO = listMapper.mapList(taskList, TaskOutputDTO.class, modelMapper);
            return ResponseEntity.ok(taskList);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer taskId) {
        TaskV2 task = service.findByID(taskId);
        return ResponseEntity.ok(task);
    }

    //    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public ResponseEntity<Object> addNewTask(@RequestBody TaskInputDTO taskDTO) {
        TaskV2 task = service.createNewTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Object> updateTask(@PathVariable Integer taskId, @RequestBody TaskInputDTO taskDTO) {
        TaskV2 task = service.updateTask(taskId, taskDTO);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Object> deleteTask(@PathVariable Integer taskId) {
        TaskOutputDTO taskWithIdDTO = service.removeTask(taskId);
        return ResponseEntity.ok(taskWithIdDTO);
    }
}

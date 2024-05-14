package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.StatusInputDTO;
import int221.integrated1backend.dtos.StatusOutputDTO;
import int221.integrated1backend.entities.Status;
import int221.integrated1backend.entities.TaskV2;
import int221.integrated1backend.services.ListMapper;
import int221.integrated1backend.services.StatusService;
import int221.integrated1backend.services.TaskV2Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v2/statuses")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
public class StatusControllerV2 {
    @Autowired
    private StatusService service;
    @Autowired
    private TaskV2Service taskService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    @GetMapping("")
    public ResponseEntity<Object> getAllStatus() {
        List<Status> statusList = service.getAllStatus();
        List<StatusOutputDTO> outputDTOList = listMapper.mapList(statusList, StatusOutputDTO.class, modelMapper);
        return ResponseEntity.ok(outputDTOList);
    }

    @PostMapping("")
    public ResponseEntity<Object> addNewStatus(@RequestBody StatusInputDTO statusInputDTO) {
        Status status = service.createNewStatus(statusInputDTO);
        StatusOutputDTO statusOutputDTO = modelMapper.map(status, StatusOutputDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(statusOutputDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStatus(@PathVariable Integer id, @RequestBody StatusInputDTO statusDTO) {
        Status status = service.updateStatus(id, statusDTO);
        StatusOutputDTO statusOutputDTO = modelMapper.map(status, StatusOutputDTO.class);
        return ResponseEntity.ok(statusOutputDTO); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteStatus(@PathVariable Integer id) {
        Status status = service.removeStatus(id);
        Map<String, Object> responseBody = new HashMap<>();
        return ResponseEntity.ok().body(responseBody);
    }

    @DeleteMapping("/{id}/{newId}")
    public ResponseEntity<Object> deleteStatus(@PathVariable Integer id, @PathVariable Integer newId) {
        List<TaskV2> taskV2List = taskService.updateStatusOfTask(id, newId);
        Status status = service.removeStatus(id);
        Map<String, Object> responseBody = new HashMap<>();
        return ResponseEntity.ok().body(responseBody);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getStatusById(@PathVariable Integer id) {
        Status status = service.findByID(id);
        return ResponseEntity.ok(status);
    }
}

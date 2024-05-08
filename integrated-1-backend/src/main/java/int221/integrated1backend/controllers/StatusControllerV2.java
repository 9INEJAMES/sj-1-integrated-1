package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.StatusInputDTO;
import int221.integrated1backend.entities.Status;
import int221.integrated1backend.services.ListMapper;
import int221.integrated1backend.services.StatusService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v2/statuses")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th:80", "http://ip23sj1.sit.kmutt.ac.th", "http://localhost:4173", "http://localhost:5173"})

public class StatusControllerV2 {
    @Autowired
    private StatusService service;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    @GetMapping("")
    public ResponseEntity<Object> getAllStatus() {
        List<Status> statusList = service.getAllStatus();
        return ResponseEntity.ok(statusList);
    }

    @PostMapping("")
    public ResponseEntity<Object> addNewStatus(@RequestBody StatusInputDTO statusInputDTO) {
        Status task = service.createNewStatus(statusInputDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStatus(@PathVariable Integer id, @RequestBody StatusInputDTO statusDTO) {
        Status status = service.updateStatus(id, statusDTO);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteStatus(@PathVariable Integer id) {
        Status status = service.removeStatus(id);
        return ResponseEntity.ok(status);
    }
}

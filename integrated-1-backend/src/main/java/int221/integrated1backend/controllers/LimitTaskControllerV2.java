package int221.integrated1backend.controllers;

import int221.integrated1backend.entities.LimitTask;
import int221.integrated1backend.services.LimitTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/limit")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
public class LimitTaskControllerV2 {
    @Autowired
    private LimitTaskService service;

    @GetMapping("")
    public ResponseEntity<Object> getLimitTask() {
        LimitTask limitTask = service.getLimitTask();
        return ResponseEntity.ok(limitTask);
    }

    @PutMapping("")
    public ResponseEntity<Object> updateLimitTask(@RequestBody LimitTask limitTaskInput) {
        LimitTask limitTask = service.updateLimitTask(limitTaskInput);
        return ResponseEntity.ok(limitTask);
    }
}

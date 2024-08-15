package int221.integrated1backend.controllers;

import int221.integrated1backend.entities.Task;
import int221.integrated1backend.entities.User;
import int221.integrated1backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("")
    public ResponseEntity<Object> getAllUser(){
        List<User> userList =  userService.getAllUser();
        return ResponseEntity.ok(userList);
    }

}

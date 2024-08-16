package int221.integrated1backend.controllers;

import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("")
    public ResponseEntity<Object> getAllUser(){
        List<User> userList =  userService.getAllUser();
        System.out.println(userList);
        return ResponseEntity.ok(userList);
    }

}

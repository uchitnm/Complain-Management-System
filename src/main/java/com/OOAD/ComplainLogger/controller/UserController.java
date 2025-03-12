package com.OOAD.ComplainLogger.controller;

import com.OOAD.ComplainLogger.model.User;
import com.OOAD.ComplainLogger.model.Role;
import com.OOAD.ComplainLogger.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/workers/{category}")
    public List<User> getWorkersByCategory(@PathVariable String category) {
        return userRepository.findByRoleAndWorkerCategory(Role.WORKER, category);
    }
}

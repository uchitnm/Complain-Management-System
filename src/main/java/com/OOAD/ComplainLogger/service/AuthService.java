package com.OOAD.ComplainLogger.service;

import com.OOAD.ComplainLogger.model.User;
import com.OOAD.ComplainLogger.model.Role;
import com.OOAD.ComplainLogger.repository.UserRepository;
import com.OOAD.ComplainLogger.exception.DuplicateUsernameException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String password, Role role, String workerCategory) {
        if (userRepository.findByUsername(username) != null) {
            throw new DuplicateUsernameException(username);
        }
        
        User user;
        if (role == Role.WORKER && workerCategory != null) {
            user = new User(username, password, role, workerCategory);
        } else {
            user = new User(username, password, role);
        }
        
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            user.setLastLoginDate(LocalDateTime.now());
            return userRepository.save(user);
        }
        return null;
    }
}

package com.OOAD.ComplainLogger.service;

import com.OOAD.ComplainLogger.model.User;
import com.OOAD.ComplainLogger.model.Role;
import com.OOAD.ComplainLogger.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String password, Role role) {
        User user = new User(username, password, role);
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}

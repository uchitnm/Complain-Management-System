package com.OOAD.ComplainLogger.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.OOAD.ComplainLogger.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

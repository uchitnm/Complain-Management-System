package com.OOAD.ComplainLogger.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.OOAD.ComplainLogger.model.User;
import com.OOAD.ComplainLogger.model.Role;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByRoleAndWorkerCategory(Role role, String workerCategory);
}

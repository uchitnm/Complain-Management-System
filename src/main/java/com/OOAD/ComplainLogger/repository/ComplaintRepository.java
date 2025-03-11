package com.OOAD.ComplainLogger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.OOAD.ComplainLogger.model.Complaint;
import com.OOAD.ComplainLogger.model.ComplaintStatus;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudentUsername(String username);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByStatusNot(ComplaintStatus status);
    List<Complaint> findByCategoryAndStatusNot(String category, ComplaintStatus status);
    List<Complaint> findByWorkerUsername(String workerUsername);
    List<Complaint> findByCategory(String category);
    List<Complaint> findByWorkerUsernameOrCategoryAndStatus(
        String workerUsername, 
        String category, 
        ComplaintStatus status
    );
}

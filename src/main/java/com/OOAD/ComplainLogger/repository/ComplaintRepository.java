package com.OOAD.ComplainLogger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.OOAD.ComplainLogger.model.Complaint;
import com.OOAD.ComplainLogger.model.ComplaintStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<Complaint> findByWorkerUsernameOrCategoryAndStatusNot(
        String workerUsername, 
        String category, 
        ComplaintStatus status
    );

    @Query("SELECT c FROM Complaint c WHERE " +
           "(:student IS NULL OR c.studentUsername LIKE %:student%) AND " +
           "(:category IS NULL OR c.category = :category) AND " +
           "(:status IS NULL OR c.status = :status)")
    List<Complaint> findWithFilters(
        @Param("student") String student,
        @Param("category") String category,
        @Param("status") ComplaintStatus status
    );

    @Query("SELECT c FROM Complaint c WHERE c.workerUsername = :username OR " +
           "(c.category = :category AND c.status = :status AND c.workerUsername IS NULL)")
    List<Complaint> findWorkerComplaints(
        @Param("username") String username,
        @Param("category") String category,
        @Param("status") ComplaintStatus status
    );
}

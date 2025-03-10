package com.OOAD.ComplainLogger.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.OOAD.ComplainLogger.model.Complaint;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudentUsername(String username);
}

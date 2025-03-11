package com.OOAD.ComplainLogger.service;

import com.OOAD.ComplainLogger.model.Complaint;
import com.OOAD.ComplainLogger.model.ComplaintStatus;
import com.OOAD.ComplainLogger.model.Role;
import com.OOAD.ComplainLogger.model.User;
import com.OOAD.ComplainLogger.repository.ComplaintRepository;
import com.OOAD.ComplainLogger.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintService(ComplaintRepository complaintRepository, UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    public Complaint logComplaint(String studentUsername, String category, String description) {
        Complaint complaint = new Complaint(studentUsername, category, description);
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getStudentComplaints(String username) {
        return complaintRepository.findByStudentUsername(username);
    }

    public List<Complaint> getWorkerComplaints(String username) {
        User worker = userRepository.findByUsername(username);
        if (worker == null || worker.getRole() != Role.WORKER) {
            throw new RuntimeException("Invalid worker username");
        }
        return complaintRepository.findWorkerComplaints(
            username,
            worker.getWorkerCategory(),
            ComplaintStatus.OPEN
        );
    }

    public List<Complaint> getComplaintsByCategory(String category) {
        return complaintRepository.findByCategory(category);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint autoAssignComplaint(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new RuntimeException("Complaint not found"));
            
        if (complaint.getWorkerUsername() != null) {
            throw new RuntimeException("Complaint already assigned");
        }

        List<User> availableWorkers = userRepository.findByRoleAndWorkerCategory(
            Role.WORKER, complaint.getCategory());
            
        if (availableWorkers.isEmpty()) {
            throw new RuntimeException("No workers available for category: " + complaint.getCategory());
        }
        
        // Simple round-robin assignment
        User worker = availableWorkers.get(0);
        complaint.setWorkerUsername(worker.getUsername());
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status);
    }

    public Complaint assignComplaint(Long complaintId, String workerUsername) {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new RuntimeException("Complaint not found"));
            
        if (complaint.getStatus() == ComplaintStatus.RESOLVED) {
            throw new RuntimeException("Cannot assign resolved complaint");
        }
        
        complaint.setWorkerUsername(workerUsername);
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        return complaintRepository.save(complaint);
    }

    public Complaint updateComplaintStatus(Long complaintId, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new RuntimeException("Complaint not found"));
            
        if (complaint.getStatus() == ComplaintStatus.RESOLVED) {
            throw new RuntimeException("Cannot update resolved complaint");
        }
        
        complaint.setStatus(status);
        if (status == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }
        return complaintRepository.save(complaint);
    }

    public Complaint updateComplaint(Long id, Complaint updatedComplaint) {
        Complaint existingComplaint = complaintRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        // Keep original student and worker information
        String originalStudent = existingComplaint.getStudentUsername();
        String originalWorker = existingComplaint.getWorkerUsername();
        
        existingComplaint.setCategory(updatedComplaint.getCategory());
        existingComplaint.setDescription(updatedComplaint.getDescription());
        existingComplaint.setStatus(updatedComplaint.getStatus());
        
        // Preserve original user information
        existingComplaint.setStudentUsername(originalStudent);
        existingComplaint.setWorkerUsername(originalWorker);
        
        if (updatedComplaint.getStatus() == ComplaintStatus.RESOLVED) {
            existingComplaint.setResolvedAt(LocalDateTime.now());
        }
        
        return complaintRepository.save(existingComplaint);
    }

    public void deleteComplaint(Long id) {
        Complaint complaint = complaintRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaintRepository.delete(complaint);
    }

    public List<Complaint> getFilteredComplaints(String student, String category, ComplaintStatus status) {
        if (student == null && category == null && status == null) {
            return complaintRepository.findAll();
        }
        return complaintRepository.findWithFilters(student, category, status);
    }
}

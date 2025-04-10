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
        try {
            List<User> availableWorkers = userRepository.findByRoleAndWorkerCategory(Role.WORKER, category);
            if (!availableWorkers.isEmpty()) {
                User selectedWorker = findLeastLoadedWorker(availableWorkers);
                complaint.setWorkerUsername(selectedWorker.getUsername());
                complaint.setStatus(ComplaintStatus.IN_PROGRESS);
            }
        } catch (Exception e) {
            System.err.println("Auto-assignment failed: " + e.getMessage());
        }
        return complaintRepository.save(complaint);
    }

    private User findLeastLoadedWorker(List<User> workers) {
        return workers.stream()
            .map(worker -> new WorkerLoad(worker, getWorkerActiveComplaintCount(worker)))
            .min((w1, w2) -> Long.compare(w1.activeComplaints, w2.activeComplaints))
            .map(workerLoad -> workerLoad.worker)
            .orElse(workers.get(0));
    }

    private long getWorkerActiveComplaintCount(User worker) {
        return complaintRepository.findByWorkerUsername(worker.getUsername())
            .stream()
            .filter(c -> c.getStatus() != ComplaintStatus.RESOLVED)
            .count();
    }

    private static class WorkerLoad {
        final User worker;
        final long activeComplaints;

        WorkerLoad(User worker, long activeComplaints) {
            this.worker = worker;
            this.activeComplaints = activeComplaints;
        }
    }

    public List<Complaint> getStudentComplaints(String username) {
        return complaintRepository.findByStudentUsername(username);
    }

    public List<Complaint> getWorkerComplaints(String username) {
        User worker = userRepository.findByUsername(username);
        if (worker == null || worker.getRole() != Role.WORKER) {
            throw new RuntimeException("Invalid worker username");
        }
        // Return assigned complaints and available complaints (not resolved)
        return complaintRepository.findByWorkerUsernameOrAvailableComplaints(
            worker.getUsername(),
            worker.getWorkerCategory()
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
        
        User selectedWorker = findLeastLoadedWorker(availableWorkers);
        complaint.setWorkerUsername(selectedWorker.getUsername());
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
        
        if (updatedComplaint.getWorkerComments() != null) {
            existingComplaint.setWorkerComments(updatedComplaint.getWorkerComments());
        }
        if (updatedComplaint.getCategory() != null) {
            existingComplaint.setCategory(updatedComplaint.getCategory());
        }
        if (updatedComplaint.getDescription() != null) {
            existingComplaint.setDescription(updatedComplaint.getDescription());
        }
        if (updatedComplaint.getStatus() != null) {
            existingComplaint.setStatus(updatedComplaint.getStatus());
        }
        
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

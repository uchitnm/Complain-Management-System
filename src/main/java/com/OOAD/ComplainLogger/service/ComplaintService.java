package com.OOAD.ComplainLogger.service;

import com.OOAD.ComplainLogger.model.Complaint;
import com.OOAD.ComplainLogger.model.ComplaintStatus;
import com.OOAD.ComplainLogger.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {
    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public Complaint logComplaint(String studentUsername, String category, String description) {
        Complaint complaint = new Complaint(studentUsername, category, description);
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getStudentComplaints(String username) {
        return complaintRepository.findByStudentUsername(username);
    }

    public Complaint updateComplaintStatus(Long complaintId, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(complaintId).orElse(null);
        if (complaint != null) {
            complaint.setStatus(status);
            if (status == ComplaintStatus.RESOLVED) {
                complaint.setResolvedAt(LocalDateTime.now());
            }
            return complaintRepository.save(complaint);
        }
        return null;
    }

    public Complaint updateComplaint(Long id, Complaint updatedComplaint) {
        Complaint existingComplaint = complaintRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        existingComplaint.setCategory(updatedComplaint.getCategory());
        existingComplaint.setDescription(updatedComplaint.getDescription());
        existingComplaint.setStatus(updatedComplaint.getStatus());
        
        if (updatedComplaint.getStatus() == ComplaintStatus.RESOLVED) {
            existingComplaint.setResolvedAt(LocalDateTime.now());
        }
        
        return complaintRepository.save(existingComplaint);
    }
}

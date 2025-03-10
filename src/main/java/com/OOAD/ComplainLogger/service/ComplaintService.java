package com.OOAD.ComplainLogger.service;

import com.OOAD.ComplainLogger.model.Complaint;
import com.OOAD.ComplainLogger.model.ComplaintStatus;
import com.OOAD.ComplainLogger.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

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
            return complaintRepository.save(complaint);
        }
        return null;
    }
}

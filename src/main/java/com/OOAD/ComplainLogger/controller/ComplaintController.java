package com.OOAD.ComplainLogger.controller;

import com.OOAD.ComplainLogger.model.Complaint;
import com.OOAD.ComplainLogger.model.ComplaintStatus;
import com.OOAD.ComplainLogger.service.ComplaintService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin
public class ComplaintController {
    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public Complaint logComplaint(@RequestBody Complaint complaint) {
        return complaintService.logComplaint(
            complaint.getStudentUsername(),
            complaint.getCategory(),
            complaint.getDescription()
        );
    }

    @GetMapping("/student/{username}")
    public List<Complaint> getStudentComplaints(@PathVariable String username) {
        return complaintService.getStudentComplaints(username);
    }

    @PutMapping("/{id}/status")
    public Complaint updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        return complaintService.updateComplaintStatus(id, request.getStatus());
    }

    @PutMapping("/{id}")
    public Complaint updateComplaint(@PathVariable Long id, @RequestBody Complaint updatedComplaint) {
        return complaintService.updateComplaint(id, updatedComplaint);
    }
}

class StatusUpdateRequest {
    private ComplaintStatus status;

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }
}

package com.OOAD.ComplainLogger.controller;

import com.OOAD.ComplainLogger.model.Complaint;
import com.OOAD.ComplainLogger.model.ComplaintStatus;
import com.OOAD.ComplainLogger.service.ComplaintService;
import com.OOAD.ComplainLogger.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/worker/{username}")
    public List<Complaint> getWorkerComplaints(@PathVariable String username) {
        return complaintService.getWorkerComplaints(username);
    }

    @GetMapping("/worker/category/{category}")
    public List<Complaint> getComplaintsByCategory(@PathVariable String category) {
        return complaintService.getComplaintsByCategory(category);
    }

    @GetMapping("/status/{status}")
    public List<Complaint> getComplaintsByStatus(@PathVariable ComplaintStatus status) {
        return complaintService.getComplaintsByStatus(status);
    }

    @PutMapping("/{id}/status")
    public Complaint updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        return complaintService.updateComplaintStatus(id, request.getStatus());
    }

    @PutMapping("/{id}")
    public Complaint updateComplaint(@PathVariable Long id, @RequestBody Complaint updatedComplaint) {
        return complaintService.updateComplaint(id, updatedComplaint);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignComplaint(@PathVariable Long id, @RequestBody String workerUsername) {
        try {
            Complaint complaint = complaintService.assignComplaint(id, workerUsername);
            return ResponseEntity.ok(complaint);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @PutMapping("/{id}/auto-assign")
    public ResponseEntity<?> autoAssignComplaint(@PathVariable Long id) {
        try {
            Complaint complaint = complaintService.autoAssignComplaint(id);
            return ResponseEntity.ok(complaint);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComplaint(@PathVariable Long id) {
        try {
            complaintService.deleteComplaint(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/filter")
    public List<Complaint> getFilteredComplaints(
        @RequestParam(required = false) String student,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) ComplaintStatus status) {
        return complaintService.getFilteredComplaints(student, category, status);
    }
}

class StatusUpdateRequest {
    private ComplaintStatus status;

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }
}

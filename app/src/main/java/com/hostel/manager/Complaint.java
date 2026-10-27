package com.hostel.manager;

public class Complaint {
    private int id;
    private int studentId;
    private String studentName;
    private String roomNumber;
    private String category;
    private String title;
    private String description;
    private String urgency; // "Low", "Medium", "High"
    private String status;  // "Pending", "In Progress", "Resolved"
    private String adminRemarks;
    private String createdAt;
    private String resolvedAt;

    public Complaint() {}

    public Complaint(int id, int studentId, String studentName, String roomNumber, String category,
                     String title, String description, String urgency, String status,
                     String adminRemarks, String createdAt, String resolvedAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.roomNumber = roomNumber;
        this.category = category;
        this.title = title;
        this.description = description;
        this.urgency = urgency;
        this.status = status;
        this.adminRemarks = adminRemarks;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(String resolvedAt) { this.resolvedAt = resolvedAt; }
}

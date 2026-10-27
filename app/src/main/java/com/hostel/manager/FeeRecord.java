package com.hostel.manager;

public class FeeRecord {
    private int id;
    private int studentId;
    private String studentName;
    private String rollNo;
    private String roomNumber;
    private String semester;
    private double rentAmount;
    private double messAmount;
    private double maintenanceAmount;
    private double totalDue;
    private double paidAmount;
    private String paymentStatus; // "Paid", "Unpaid", "Partial"
    private String transactionId;
    private String paymentMethod;
    private String paymentDate;
    private String receiptNo;

    public FeeRecord() {}

    public FeeRecord(int id, int studentId, String studentName, String rollNo, String roomNumber,
                     String semester, double rentAmount, double messAmount, double maintenanceAmount,
                     double totalDue, double paidAmount, String paymentStatus, String transactionId,
                     String paymentMethod, String paymentDate, String receiptNo) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.roomNumber = roomNumber;
        this.semester = semester;
        this.rentAmount = rentAmount;
        this.messAmount = messAmount;
        this.maintenanceAmount = maintenanceAmount;
        this.totalDue = totalDue;
        this.paidAmount = paidAmount;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.receiptNo = receiptNo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public double getRentAmount() { return rentAmount; }
    public void setRentAmount(double rentAmount) { this.rentAmount = rentAmount; }

    public double getMessAmount() { return messAmount; }
    public void setMessAmount(double messAmount) { this.messAmount = messAmount; }

    public double getMaintenanceAmount() { return maintenanceAmount; }
    public void setMaintenanceAmount(double maintenanceAmount) { this.maintenanceAmount = maintenanceAmount; }

    public double getTotalDue() { return totalDue; }
    public void setTotalDue(double totalDue) { this.totalDue = totalDue; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public double getBalanceDue() {
        return Math.max(0, totalDue - paidAmount);
    }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getReceiptNo() { return receiptNo; }
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
}

package com.hostel.manager;

public class User {
    private int id;
    private String role; // "student" or "admin"
    private String name;
    private String email;
    private String phone;
    private String rollNo;
    private String password;
    private int roomId;
    private String roomNumber;
    private String gender;
    private String createdAt;

    public User() {}

    public User(int id, String role, String name, String email, String phone, String rollNo, String password, int roomId, String roomNumber, String gender, String createdAt) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.rollNo = rollNo;
        this.password = password;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.gender = gender;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

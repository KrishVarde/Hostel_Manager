package com.hostel.manager;

public class Room {
    private int id;
    private String roomNumber;
    private String block;
    private int floor;
    private String roomType; // "AC" or "Non-AC"
    private int capacity;
    private int occupied;
    private double feePerSem;

    public Room() {}

    public Room(int id, String roomNumber, String block, int floor, String roomType, int capacity, int occupied, double feePerSem) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.block = block;
        this.floor = floor;
        this.roomType = roomType;
        this.capacity = capacity;
        this.occupied = occupied;
        this.feePerSem = feePerSem;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getOccupied() { return occupied; }
    public void setOccupied(int occupied) { this.occupied = occupied; }

    public double getFeePerSem() { return feePerSem; }
    public void setFeePerSem(double feePerSem) { this.feePerSem = feePerSem; }

    public boolean isAvailable() {
        return occupied < capacity;
    }

    public int getAvailableBeds() {
        return Math.max(0, capacity - occupied);
    }
}

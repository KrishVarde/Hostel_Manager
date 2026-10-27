package com.hostel.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HostelDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hostel_manager.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ROOMS = "rooms";
    public static final String TABLE_COMPLAINTS = "complaints";
    public static final String TABLE_FEES = "fees";

    // User columns
    public static final String U_ID = "id";
    public static final String U_ROLE = "role";
    public static final String U_NAME = "name";
    public static final String U_EMAIL = "email";
    public static final String U_PHONE = "phone";
    public static final String U_ROLL_NO = "roll_no";
    public static final String U_PASSWORD = "password";
    public static final String U_ROOM_ID = "room_id";
    public static final String U_GENDER = "gender";
    public static final String U_CREATED_AT = "created_at";

    // Room columns
    public static final String R_ID = "id";
    public static final String R_ROOM_NUMBER = "room_number";
    public static final String R_BLOCK = "block";
    public static final String R_FLOOR = "floor";
    public static final String R_ROOM_TYPE = "room_type";
    public static final String R_CAPACITY = "capacity";
    public static final String R_OCCUPIED = "occupied";
    public static final String R_FEE_PER_SEM = "fee_per_sem";

    // Complaint columns
    public static final String C_ID = "id";
    public static final String C_STUDENT_ID = "student_id";
    public static final String C_STUDENT_NAME = "student_name";
    public static final String C_ROOM_NUMBER = "room_number";
    public static final String C_CATEGORY = "category";
    public static final String C_TITLE = "title";
    public static final String C_DESCRIPTION = "description";
    public static final String C_URGENCY = "urgency";
    public static final String C_STATUS = "status";
    public static final String C_ADMIN_REMARKS = "admin_remarks";
    public static final String C_CREATED_AT = "created_at";
    public static final String C_RESOLVED_AT = "resolved_at";

    // Fee columns
    public static final String F_ID = "id";
    public static final String F_STUDENT_ID = "student_id";
    public static final String F_STUDENT_NAME = "student_name";
    public static final String F_ROLL_NO = "roll_no";
    public static final String F_ROOM_NUMBER = "room_number";
    public static final String F_SEMESTER = "semester";
    public static final String F_RENT_AMOUNT = "rent_amount";
    public static final String F_MESS_AMOUNT = "mess_amount";
    public static final String F_MAINTENANCE_AMOUNT = "maintenance_amount";
    public static final String F_TOTAL_DUE = "total_due";
    public static final String F_PAID_AMOUNT = "paid_amount";
    public static final String F_PAYMENT_STATUS = "payment_status";
    public static final String F_TRANSACTION_ID = "transaction_id";
    public static final String F_PAYMENT_METHOD = "payment_method";
    public static final String F_PAYMENT_DATE = "payment_date";
    public static final String F_RECEIPT_NO = "receipt_no";

    public HostelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                U_ROLE + " TEXT, " +
                U_NAME + " TEXT, " +
                U_EMAIL + " TEXT UNIQUE, " +
                U_PHONE + " TEXT, " +
                U_ROLL_NO + " TEXT, " +
                U_PASSWORD + " TEXT, " +
                U_ROOM_ID + " INTEGER DEFAULT 0, " +
                U_GENDER + " TEXT, " +
                U_CREATED_AT + " TEXT);";

        String createRooms = "CREATE TABLE " + TABLE_ROOMS + " (" +
                R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                R_ROOM_NUMBER + " TEXT UNIQUE, " +
                R_BLOCK + " TEXT, " +
                R_FLOOR + " INTEGER, " +
                R_ROOM_TYPE + " TEXT, " +
                R_CAPACITY + " INTEGER, " +
                R_OCCUPIED + " INTEGER DEFAULT 0, " +
                R_FEE_PER_SEM + " REAL);";

        String createComplaints = "CREATE TABLE " + TABLE_COMPLAINTS + " (" +
                C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_STUDENT_ID + " INTEGER, " +
                C_STUDENT_NAME + " TEXT, " +
                C_ROOM_NUMBER + " TEXT, " +
                C_CATEGORY + " TEXT, " +
                C_TITLE + " TEXT, " +
                C_DESCRIPTION + " TEXT, " +
                C_URGENCY + " TEXT, " +
                C_STATUS + " TEXT, " +
                C_ADMIN_REMARKS + " TEXT, " +
                C_CREATED_AT + " TEXT, " +
                C_RESOLVED_AT + " TEXT);";

        String createFees = "CREATE TABLE " + TABLE_FEES + " (" +
                F_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                F_STUDENT_ID + " INTEGER, " +
                F_STUDENT_NAME + " TEXT, " +
                F_ROLL_NO + " TEXT, " +
                F_ROOM_NUMBER + " TEXT, " +
                F_SEMESTER + " TEXT, " +
                F_RENT_AMOUNT + " REAL, " +
                F_MESS_AMOUNT + " REAL, " +
                F_MAINTENANCE_AMOUNT + " REAL, " +
                F_TOTAL_DUE + " REAL, " +
                F_PAID_AMOUNT + " REAL DEFAULT 0, " +
                F_PAYMENT_STATUS + " TEXT, " +
                F_TRANSACTION_ID + " TEXT, " +
                F_PAYMENT_METHOD + " TEXT, " +
                F_PAYMENT_DATE + " TEXT, " +
                F_RECEIPT_NO + " TEXT);";

        db.execSQL(createUsers);
        db.execSQL(createRooms);
        db.execSQL(createComplaints);
        db.execSQL(createFees);

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPLAINTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEES);
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        // 1. Seed Admin
        ContentValues adminVal = new ContentValues();
        adminVal.put(U_ROLE, "admin");
        adminVal.put(U_NAME, "Chief Warden Dr. K. Raman");
        adminVal.put(U_EMAIL, "admin@hostel.com");
        adminVal.put(U_PHONE, "9876500001");
        adminVal.put(U_ROLL_NO, "ADMIN-01");
        adminVal.put(U_PASSWORD, "admin123");
        adminVal.put(U_ROOM_ID, 0);
        adminVal.put(U_GENDER, "Male");
        adminVal.put(U_CREATED_AT, now);
        db.insert(TABLE_USERS, null, adminVal);

        // 2. Seed Rooms
        insertRoomDirect(db, "101-A", "Block A (Boys)", 1, "AC", 2, 1, 30000.0);
        insertRoomDirect(db, "102-A", "Block A (Boys)", 1, "Non-AC", 2, 0, 22000.0);
        insertRoomDirect(db, "102-B", "Block B (Boys)", 1, "Non-AC", 3, 1, 20000.0);
        insertRoomDirect(db, "201-B", "Block B (Boys)", 2, "AC", 2, 0, 28000.0);
        insertRoomDirect(db, "301-C", "Block C (Girls)", 3, "AC", 2, 1, 32000.0);
        insertRoomDirect(db, "302-C", "Block C (Girls)", 3, "Non-AC", 2, 0, 24000.0);

        // 3. Seed Students
        // Rahul Sharma
        ContentValues s1 = new ContentValues();
        s1.put(U_ROLE, "student");
        s1.put(U_NAME, "Rahul Sharma");
        s1.put(U_EMAIL, "student@hostel.com");
        s1.put(U_PHONE, "9876543210");
        s1.put(U_ROLL_NO, "CS2024-042");
        s1.put(U_PASSWORD, "student123");
        s1.put(U_ROOM_ID, 1); // 101-A
        s1.put(U_GENDER, "Male");
        s1.put(U_CREATED_AT, now);
        long s1Id = db.insert(TABLE_USERS, null, s1);

        // Priya Patel
        ContentValues s2 = new ContentValues();
        s2.put(U_ROLE, "student");
        s2.put(U_NAME, "Priya Patel");
        s2.put(U_EMAIL, "priya@hostel.com");
        s2.put(U_PHONE, "9812345678");
        s2.put(U_ROLL_NO, "IT2024-018");
        s2.put(U_PASSWORD, "priya123");
        s2.put(U_ROOM_ID, 5); // 301-C
        s2.put(U_GENDER, "Female");
        s2.put(U_CREATED_AT, now);
        long s2Id = db.insert(TABLE_USERS, null, s2);

        // Amit Kumar
        ContentValues s3 = new ContentValues();
        s3.put(U_ROLE, "student");
        s3.put(U_NAME, "Amit Kumar");
        s3.put(U_EMAIL, "amit@hostel.com");
        s3.put(U_PHONE, "9823456789");
        s3.put(U_ROLL_NO, "EC2024-089");
        s3.put(U_PASSWORD, "amit123");
        s3.put(U_ROOM_ID, 3); // 102-B
        s3.put(U_GENDER, "Male");
        s3.put(U_CREATED_AT, now);
        long s3Id = db.insert(TABLE_USERS, null, s3);

        // 4. Seed Complaints
        insertComplaintDirect(db, (int)s1Id, "Rahul Sharma", "101-A", "Plumbing",
                "Bathroom tap is leaking constantly",
                "The bathroom faucet in room 101-A is dripping heavily causing water waste.",
                "Medium", "In Progress", "Plumber assigned, work in progress", "2026-10-20 10:15", "");

        insertComplaintDirect(db, (int)s1Id, "Rahul Sharma", "101-A", "Wi-Fi & Internet",
                "High packet drop on 1st floor Wi-Fi router",
                "Hostel Wi-Fi disconnects frequently during online lectures.",
                "High", "Pending", "", "2026-10-21 14:30", "");

        insertComplaintDirect(db, (int)s2Id, "Priya Patel", "301-C", "Electrical",
                "Ceiling fan making clicking noise",
                "The fan speed regulator and bearing makes loud vibrating noise at night.",
                "Low", "Resolved", "Capacitor and bearing replaced on 20-Oct", "2026-10-18 09:00", "2026-10-20 16:00");

        // 5. Seed Fees
        // Rahul: Partial
        insertFeeDirect(db, (int)s1Id, "Rahul Sharma", "CS2024-042", "101-A", "Sem 5 (Odd 2026)",
                30000.0, 15000.0, 2000.0, 47000.0, 20000.0, "Partial", "TXN982410294", "UPI", "2026-10-05", "REC-2026-1042");

        // Priya: Paid
        insertFeeDirect(db, (int)s2Id, "Priya Patel", "IT2024-018", "301-C", "Sem 5 (Odd 2026)",
                32000.0, 15000.0, 2000.0, 49000.0, 49000.0, "Paid", "TXN872149502", "Net Banking", "2026-09-28", "REC-2026-0918");

        // Amit: Unpaid
        insertFeeDirect(db, (int)s3Id, "Amit Kumar", "EC2024-089", "102-B", "Sem 5 (Odd 2026)",
                20000.0, 15000.0, 2000.0, 37000.0, 0.0, "Unpaid", "", "", "", "");
    }

    private void insertRoomDirect(SQLiteDatabase db, String num, String block, int floor, String type, int cap, int occ, double fee) {
        ContentValues cv = new ContentValues();
        cv.put(R_ROOM_NUMBER, num);
        cv.put(R_BLOCK, block);
        cv.put(R_FLOOR, floor);
        cv.put(R_ROOM_TYPE, type);
        cv.put(R_CAPACITY, cap);
        cv.put(R_OCCUPIED, occ);
        cv.put(R_FEE_PER_SEM, fee);
        db.insert(TABLE_ROOMS, null, cv);
    }

    private void insertComplaintDirect(SQLiteDatabase db, int sId, String sName, String rNum, String cat, String title, String desc, String urgency, String status, String remarks, String createdAt, String resAt) {
        ContentValues cv = new ContentValues();
        cv.put(C_STUDENT_ID, sId);
        cv.put(C_STUDENT_NAME, sName);
        cv.put(C_ROOM_NUMBER, rNum);
        cv.put(C_CATEGORY, cat);
        cv.put(C_TITLE, title);
        cv.put(C_DESCRIPTION, desc);
        cv.put(C_URGENCY, urgency);
        cv.put(C_STATUS, status);
        cv.put(C_ADMIN_REMARKS, remarks);
        cv.put(C_CREATED_AT, createdAt);
        cv.put(C_RESOLVED_AT, resAt);
        db.insert(TABLE_COMPLAINTS, null, cv);
    }

    private void insertFeeDirect(SQLiteDatabase db, int sId, String sName, String roll, String rNum, String sem, double rent, double mess, double maint, double total, double paid, String status, String txnId, String method, String pDate, String recNo) {
        ContentValues cv = new ContentValues();
        cv.put(F_STUDENT_ID, sId);
        cv.put(F_STUDENT_NAME, sName);
        cv.put(F_ROLL_NO, roll);
        cv.put(F_ROOM_NUMBER, rNum);
        cv.put(F_SEMESTER, sem);
        cv.put(F_RENT_AMOUNT, rent);
        cv.put(F_MESS_AMOUNT, mess);
        cv.put(F_MAINTENANCE_AMOUNT, maint);
        cv.put(F_TOTAL_DUE, total);
        cv.put(F_PAID_AMOUNT, paid);
        cv.put(F_PAYMENT_STATUS, status);
        cv.put(F_TRANSACTION_ID, txnId);
        cv.put(F_PAYMENT_METHOD, method);
        cv.put(F_PAYMENT_DATE, pDate);
        cv.put(F_RECEIPT_NO, recNo);
        db.insert(TABLE_FEES, null, cv);
    }

    // ==================== AUTH & USER CRUD ====================
    public long registerStudent(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_ROLE, "student");
        cv.put(U_NAME, user.getName());
        cv.put(U_EMAIL, user.getEmail().trim().toLowerCase());
        cv.put(U_PHONE, user.getPhone());
        cv.put(U_ROLL_NO, user.getRollNo());
        cv.put(U_PASSWORD, user.getPassword());
        cv.put(U_ROOM_ID, 0);
        cv.put(U_GENDER, user.getGender());
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        cv.put(U_CREATED_AT, now);
        long id = db.insert(TABLE_USERS, null, cv);

        if (id != -1) {
            // Automatically initialize a standard semester fee record for the new student
            ContentValues fv = new ContentValues();
            fv.put(F_STUDENT_ID, id);
            fv.put(F_STUDENT_NAME, user.getName());
            fv.put(F_ROLL_NO, user.getRollNo());
            fv.put(F_ROOM_NUMBER, "Not Allocated");
            fv.put(F_SEMESTER, "Sem 5 (Odd 2026)");
            fv.put(F_RENT_AMOUNT, 25000.0);
            fv.put(F_MESS_AMOUNT, 15000.0);
            fv.put(F_MAINTENANCE_AMOUNT, 2000.0);
            fv.put(F_TOTAL_DUE, 42000.0);
            fv.put(F_PAID_AMOUNT, 0.0);
            fv.put(F_PAYMENT_STATUS, "Unpaid");
            fv.put(F_TRANSACTION_ID, "");
            fv.put(F_PAYMENT_METHOD, "");
            fv.put(F_PAYMENT_DATE, "");
            fv.put(F_RECEIPT_NO, "");
            db.insert(TABLE_FEES, null, fv);
        }
        return id;
    }

    public User loginUser(String email, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.*, r." + R_ROOM_NUMBER + " FROM " + TABLE_USERS + " u " +
                "LEFT JOIN " + TABLE_ROOMS + " r ON u." + U_ROOM_ID + " = r." + R_ID + " " +
                "WHERE LOWER(u." + U_EMAIL + ") = LOWER(?) AND u." + U_PASSWORD + " = ? AND u." + U_ROLE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email.trim(), password, role});
        User user = null;
        if (cursor.moveToFirst()) {
            user = extractUserFromCursor(cursor);
            int roomNumIdx = cursor.getColumnIndex(R_ROOM_NUMBER);
            if (roomNumIdx != -1 && !cursor.isNull(roomNumIdx)) {
                user.setRoomNumber(cursor.getString(roomNumIdx));
            } else {
                user.setRoomNumber("Not Allocated");
            }
        }
        cursor.close();
        return user;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.*, r." + R_ROOM_NUMBER + " FROM " + TABLE_USERS + " u " +
                "LEFT JOIN " + TABLE_ROOMS + " r ON u." + U_ROOM_ID + " = r." + R_ID + " " +
                "WHERE u." + U_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        User user = null;
        if (cursor.moveToFirst()) {
            user = extractUserFromCursor(cursor);
            int roomNumIdx = cursor.getColumnIndex(R_ROOM_NUMBER);
            if (roomNumIdx != -1 && !cursor.isNull(roomNumIdx)) {
                user.setRoomNumber(cursor.getString(roomNumIdx));
            } else {
                user.setRoomNumber("Not Allocated");
            }
        }
        cursor.close();
        return user;
    }

    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_USERS + " WHERE LOWER(" + U_EMAIL + ") = LOWER(?)",
                new String[]{email.trim()});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public List<User> getAllStudents() {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.*, r." + R_ROOM_NUMBER + " FROM " + TABLE_USERS + " u " +
                "LEFT JOIN " + TABLE_ROOMS + " r ON u." + U_ROOM_ID + " = r." + R_ID + " " +
                "WHERE u." + U_ROLE + " = 'student' ORDER BY u." + U_NAME + " ASC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                User u = extractUserFromCursor(cursor);
                int roomNumIdx = cursor.getColumnIndex(R_ROOM_NUMBER);
                if (roomNumIdx != -1 && !cursor.isNull(roomNumIdx)) {
                    u.setRoomNumber(cursor.getString(roomNumIdx));
                } else {
                    u.setRoomNumber("Not Allocated");
                }
                list.add(u);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean updateUserProfile(int userId, String name, String phone, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_NAME, name);
        cv.put(U_PHONE, phone);
        cv.put(U_GENDER, gender);
        return db.update(TABLE_USERS, cv, U_ID + "=?", new String[]{String.valueOf(userId)}) > 0;
    }

    // ==================== ROOM CRUD ====================
    public long addRoom(Room r) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(R_ROOM_NUMBER, r.getRoomNumber());
        cv.put(R_BLOCK, r.getBlock());
        cv.put(R_FLOOR, r.getFloor());
        cv.put(R_ROOM_TYPE, r.getRoomType());
        cv.put(R_CAPACITY, r.getCapacity());
        cv.put(R_OCCUPIED, 0);
        cv.put(R_FEE_PER_SEM, r.getFeePerSem());
        return db.insert(TABLE_ROOMS, null, cv);
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ROOMS + " ORDER BY " + R_ROOM_NUMBER + " ASC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(extractRoomFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Room getRoomById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ROOMS + " WHERE " + R_ID + "=?", new String[]{String.valueOf(id)});
        Room r = null;
        if (cursor.moveToFirst()) {
            r = extractRoomFromCursor(cursor);
        }
        cursor.close();
        return r;
    }

    public boolean allocateStudentToRoom(int studentId, int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // First get previous room of student if any
            Cursor cUser = db.rawQuery("SELECT " + U_ROOM_ID + " FROM " + TABLE_USERS + " WHERE " + U_ID + "=?", new String[]{String.valueOf(studentId)});
            int oldRoomId = 0;
            if (cUser.moveToFirst()) {
                oldRoomId = cUser.getInt(0);
            }
            cUser.close();

            if (oldRoomId > 0 && oldRoomId != roomId) {
                // Decrement old room occupied
                db.execSQL("UPDATE " + TABLE_ROOMS + " SET " + R_OCCUPIED + " = MAX(0, " + R_OCCUPIED + " - 1) WHERE " + R_ID + "=" + oldRoomId);
            }

            // Update user room_id
            ContentValues cvUser = new ContentValues();
            cvUser.put(U_ROOM_ID, roomId);
            db.update(TABLE_USERS, cvUser, U_ID + "=?", new String[]{String.valueOf(studentId)});

            // Increment new room occupied
            db.execSQL("UPDATE " + TABLE_ROOMS + " SET " + R_OCCUPIED + " = MIN(" + R_CAPACITY + ", " + R_OCCUPIED + " + 1) WHERE " + R_ID + "=" + roomId);

            // Fetch new room number to update fee table
            Cursor cRoom = db.rawQuery("SELECT " + R_ROOM_NUMBER + " FROM " + TABLE_ROOMS + " WHERE " + R_ID + "=?", new String[]{String.valueOf(roomId)});
            if (cRoom.moveToFirst()) {
                String roomNo = cRoom.getString(0);
                ContentValues cvFee = new ContentValues();
                cvFee.put(F_ROOM_NUMBER, roomNo);
                db.update(TABLE_FEES, cvFee, F_STUDENT_ID + "=?", new String[]{String.valueOf(studentId)});
            }
            cRoom.close();

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public boolean deallocateStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor cUser = db.rawQuery("SELECT " + U_ROOM_ID + " FROM " + TABLE_USERS + " WHERE " + U_ID + "=?", new String[]{String.valueOf(studentId)});
            int oldRoomId = 0;
            if (cUser.moveToFirst()) {
                oldRoomId = cUser.getInt(0);
            }
            cUser.close();

            if (oldRoomId > 0) {
                db.execSQL("UPDATE " + TABLE_ROOMS + " SET " + R_OCCUPIED + " = MAX(0, " + R_OCCUPIED + " - 1) WHERE " + R_ID + "=" + oldRoomId);
            }

            ContentValues cvUser = new ContentValues();
            cvUser.put(U_ROOM_ID, 0);
            db.update(TABLE_USERS, cvUser, U_ID + "=?", new String[]{String.valueOf(studentId)});

            ContentValues cvFee = new ContentValues();
            cvFee.put(F_ROOM_NUMBER, "Not Allocated");
            db.update(TABLE_FEES, cvFee, F_STUDENT_ID + "=?", new String[]{String.valueOf(studentId)});

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public List<User> getRoommates(int roomId, int excludeStudentId) {
        List<User> list = new ArrayList<>();
        if (roomId <= 0) return list;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + U_ROOM_ID + "=? AND " + U_ID + "!=? AND " + U_ROLE + "='student'";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(roomId), String.valueOf(excludeStudentId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(extractUserFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean deleteRoom(int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Reset students assigned to this room
        ContentValues cv = new ContentValues();
        cv.put(U_ROOM_ID, 0);
        db.update(TABLE_USERS, cv, U_ROOM_ID + "=?", new String[]{String.valueOf(roomId)});
        return db.delete(TABLE_ROOMS, R_ID + "=?", new String[]{String.valueOf(roomId)}) > 0;
    }

    // ==================== COMPLAINTS CRUD ====================
    public long addComplaint(Complaint c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(C_STUDENT_ID, c.getStudentId());
        cv.put(C_STUDENT_NAME, c.getStudentName());
        cv.put(C_ROOM_NUMBER, c.getRoomNumber());
        cv.put(C_CATEGORY, c.getCategory());
        cv.put(C_TITLE, c.getTitle());
        cv.put(C_DESCRIPTION, c.getDescription());
        cv.put(C_URGENCY, c.getUrgency());
        cv.put(C_STATUS, "Pending");
        cv.put(C_ADMIN_REMARKS, "");
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        cv.put(C_CREATED_AT, now);
        cv.put(C_RESOLVED_AT, "");
        return db.insert(TABLE_COMPLAINTS, null, cv);
    }

    public List<Complaint> getAllComplaints() {
        List<Complaint> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COMPLAINTS + " ORDER BY " + C_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(extractComplaintFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Complaint> getComplaintsByStudent(int studentId) {
        List<Complaint> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COMPLAINTS + " WHERE " + C_STUDENT_ID + "=? ORDER BY " + C_ID + " DESC",
                new String[]{String.valueOf(studentId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(extractComplaintFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Complaint getComplaintById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COMPLAINTS + " WHERE " + C_ID + "=?", new String[]{String.valueOf(id)});
        Complaint c = null;
        if (cursor.moveToFirst()) {
            c = extractComplaintFromCursor(cursor);
        }
        cursor.close();
        return c;
    }

    public boolean updateComplaintStatus(int id, String status, String remarks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(C_STATUS, status);
        cv.put(C_ADMIN_REMARKS, remarks);
        if ("Resolved".equalsIgnoreCase(status)) {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            cv.put(C_RESOLVED_AT, now);
        }
        return db.update(TABLE_COMPLAINTS, cv, C_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteComplaint(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_COMPLAINTS, C_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    // ==================== FEES CRUD ====================
    public FeeRecord getFeeByStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FEES + " WHERE " + F_STUDENT_ID + "=? ORDER BY " + F_ID + " DESC LIMIT 1",
                new String[]{String.valueOf(studentId)});
        FeeRecord f = null;
        if (cursor.moveToFirst()) {
            f = extractFeeFromCursor(cursor);
        }
        cursor.close();
        return f;
    }

    public List<FeeRecord> getAllFeeRecords() {
        List<FeeRecord> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FEES + " ORDER BY " + F_STUDENT_NAME + " ASC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(extractFeeFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean recordPayment(int feeId, double amountToPay, String method, String txnId, String receiptNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + F_TOTAL_DUE + ", " + F_PAID_AMOUNT + " FROM " + TABLE_FEES + " WHERE " + F_ID + "=?",
                new String[]{String.valueOf(feeId)});
        if (!c.moveToFirst()) {
            c.close();
            return false;
        }
        double totalDue = c.getDouble(0);
        double currentPaid = c.getDouble(1);
        c.close();

        double newPaid = currentPaid + amountToPay;
        String newStatus;
        if (newPaid >= totalDue) {
            newPaid = totalDue;
            newStatus = "Paid";
        } else {
            newStatus = "Partial";
        }

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        ContentValues cv = new ContentValues();
        cv.put(F_PAID_AMOUNT, newPaid);
        cv.put(F_PAYMENT_STATUS, newStatus);
        cv.put(F_TRANSACTION_ID, txnId);
        cv.put(F_PAYMENT_METHOD, method);
        cv.put(F_PAYMENT_DATE, now);
        cv.put(F_RECEIPT_NO, receiptNo);

        return db.update(TABLE_FEES, cv, F_ID + "=?", new String[]{String.valueOf(feeId)}) > 0;
    }

    // ==================== DASHBOARD / ADMIN STATS ====================
    public int getTotalStudentsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + U_ROLE + "='student'", null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getTotalRoomsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ROOMS, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getActiveComplaintsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_COMPLAINTS + " WHERE " + C_STATUS + "!='Resolved'", null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public double getTotalFeesCollected() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + F_PAID_AMOUNT + ") FROM " + TABLE_FEES, null);
        double sum = 0;
        if (c.moveToFirst() && !c.isNull(0)) sum = c.getDouble(0);
        c.close();
        return sum;
    }

    // ==================== HELPER CURSOR EXTRACTORS ====================
    private User extractUserFromCursor(Cursor c) {
        User u = new User();
        u.setId(c.getInt(c.getColumnIndexOrThrow(U_ID)));
        u.setRole(c.getString(c.getColumnIndexOrThrow(U_ROLE)));
        u.setName(c.getString(c.getColumnIndexOrThrow(U_NAME)));
        u.setEmail(c.getString(c.getColumnIndexOrThrow(U_EMAIL)));
        u.setPhone(c.getString(c.getColumnIndexOrThrow(U_PHONE)));
        u.setRollNo(c.getString(c.getColumnIndexOrThrow(U_ROLL_NO)));
        u.setPassword(c.getString(c.getColumnIndexOrThrow(U_PASSWORD)));
        u.setRoomId(c.getInt(c.getColumnIndexOrThrow(U_ROOM_ID)));
        u.setGender(c.getString(c.getColumnIndexOrThrow(U_GENDER)));
        u.setCreatedAt(c.getString(c.getColumnIndexOrThrow(U_CREATED_AT)));
        return u;
    }

    private Room extractRoomFromCursor(Cursor c) {
        Room r = new Room();
        r.setId(c.getInt(c.getColumnIndexOrThrow(R_ID)));
        r.setRoomNumber(c.getString(c.getColumnIndexOrThrow(R_ROOM_NUMBER)));
        r.setBlock(c.getString(c.getColumnIndexOrThrow(R_BLOCK)));
        r.setFloor(c.getInt(c.getColumnIndexOrThrow(R_FLOOR)));
        r.setRoomType(c.getString(c.getColumnIndexOrThrow(R_ROOM_TYPE)));
        r.setCapacity(c.getInt(c.getColumnIndexOrThrow(R_CAPACITY)));
        r.setOccupied(c.getInt(c.getColumnIndexOrThrow(R_OCCUPIED)));
        r.setFeePerSem(c.getDouble(c.getColumnIndexOrThrow(R_FEE_PER_SEM)));
        return r;
    }

    private Complaint extractComplaintFromCursor(Cursor c) {
        Complaint comp = new Complaint();
        comp.setId(c.getInt(c.getColumnIndexOrThrow(C_ID)));
        comp.setStudentId(c.getInt(c.getColumnIndexOrThrow(C_STUDENT_ID)));
        comp.setStudentName(c.getString(c.getColumnIndexOrThrow(C_STUDENT_NAME)));
        comp.setRoomNumber(c.getString(c.getColumnIndexOrThrow(C_ROOM_NUMBER)));
        comp.setCategory(c.getString(c.getColumnIndexOrThrow(C_CATEGORY)));
        comp.setTitle(c.getString(c.getColumnIndexOrThrow(C_TITLE)));
        comp.setDescription(c.getString(c.getColumnIndexOrThrow(C_DESCRIPTION)));
        comp.setUrgency(c.getString(c.getColumnIndexOrThrow(C_URGENCY)));
        comp.setStatus(c.getString(c.getColumnIndexOrThrow(C_STATUS)));
        comp.setAdminRemarks(c.getString(c.getColumnIndexOrThrow(C_ADMIN_REMARKS)));
        comp.setCreatedAt(c.getString(c.getColumnIndexOrThrow(C_CREATED_AT)));
        comp.setResolvedAt(c.getString(c.getColumnIndexOrThrow(C_RESOLVED_AT)));
        return comp;
    }

    private FeeRecord extractFeeFromCursor(Cursor c) {
        FeeRecord f = new FeeRecord();
        f.setId(c.getInt(c.getColumnIndexOrThrow(F_ID)));
        f.setStudentId(c.getInt(c.getColumnIndexOrThrow(F_STUDENT_ID)));
        f.setStudentName(c.getString(c.getColumnIndexOrThrow(F_STUDENT_NAME)));
        f.setRollNo(c.getString(c.getColumnIndexOrThrow(F_ROLL_NO)));
        f.setRoomNumber(c.getString(c.getColumnIndexOrThrow(F_ROOM_NUMBER)));
        f.setSemester(c.getString(c.getColumnIndexOrThrow(F_SEMESTER)));
        f.setRentAmount(c.getDouble(c.getColumnIndexOrThrow(F_RENT_AMOUNT)));
        f.setMessAmount(c.getDouble(c.getColumnIndexOrThrow(F_MESS_AMOUNT)));
        f.setMaintenanceAmount(c.getDouble(c.getColumnIndexOrThrow(F_MAINTENANCE_AMOUNT)));
        f.setTotalDue(c.getDouble(c.getColumnIndexOrThrow(F_TOTAL_DUE)));
        f.setPaidAmount(c.getDouble(c.getColumnIndexOrThrow(F_PAID_AMOUNT)));
        f.setPaymentStatus(c.getString(c.getColumnIndexOrThrow(F_PAYMENT_STATUS)));
        f.setTransactionId(c.getString(c.getColumnIndexOrThrow(F_TRANSACTION_ID)));
        f.setPaymentMethod(c.getString(c.getColumnIndexOrThrow(F_PAYMENT_METHOD)));
        f.setPaymentDate(c.getString(c.getColumnIndexOrThrow(F_PAYMENT_DATE)));
        f.setReceiptNo(c.getString(c.getColumnIndexOrThrow(F_RECEIPT_NO)));
        return f;
    }
}

package com.hostel.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "HostelManagerSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_ROLE = "role";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLL_NO = "rollNo";
    private static final String KEY_ROOM_NUMBER = "roomNumber";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String role, String name, String email, String rollNo, String roomNumber) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLL_NO, rollNo != null ? rollNo : "");
        editor.putString(KEY_ROOM_NUMBER, roomNumber != null ? roomNumber : "Not Allocated");
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "student");
    }

    public String getName() {
        return pref.getString(KEY_NAME, "User");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public String getRollNo() {
        return pref.getString(KEY_ROLL_NO, "N/A");
    }

    public String getRoomNumber() {
        return pref.getString(KEY_ROOM_NUMBER, "Not Allocated");
    }

    public void updateRoomNumber(String newRoom) {
        editor.putString(KEY_ROOM_NUMBER, newRoom);
        editor.apply();
    }

    public void updateName(String newName) {
        editor.putString(KEY_NAME, newName);
        editor.apply();
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}

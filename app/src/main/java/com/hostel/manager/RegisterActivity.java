package com.hostel.manager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etRollNo, etEmail, etPhone, etPassword, etConfirmPassword;
    private RadioGroup rgGender;
    private Button btnRegister;
    private TextView tvSignIn;
    private HostelDbHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new HostelDbHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etRollNo = findViewById(R.id.etRollNo);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rgGender = findViewById(R.id.rgGender);
        btnRegister = findViewById(R.id.btnRegister);
        tvSignIn = findViewById(R.id.tvSignIn);

        ImageButton btnBack = findViewById(R.id.btnBackRegister);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnRegister.setOnClickListener(v -> performRegistration());
        tvSignIn.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String name = etName.getText().toString().trim();
        String rollNo = etRollNo.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        RadioButton rbGender = findViewById(selectedGenderId);
        String gender = rbGender != null ? rbGender.getText().toString() : "Male";

        if (TextUtils.isEmpty(name)) {
            etName.setError("Full Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(rollNo)) {
            etRollNo.setError("Roll number is required");
            etRollNo.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email address is required");
            etEmail.requestFocus();
            return;
        }

        if (dbHelper.isEmailRegistered(email)) {
            etEmail.setError("Email is already registered! Please login.");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            etPhone.setError("Valid 10-digit phone number is required");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setRollNo(rollNo);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setGender(gender);
        newUser.setPassword(password);

        long newId = dbHelper.registerStudent(newUser);
        if (newId != -1) {
            Toast.makeText(this, "Registration Successful! Welcome to Hostel Manager.", Toast.LENGTH_SHORT).show();
            sessionManager.createLoginSession((int) newId, "student", name, email, rollNo, "Not Allocated");

            Intent intent = new Intent(RegisterActivity.this, StudentMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}

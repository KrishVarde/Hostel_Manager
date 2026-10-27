package com.hostel.manager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private String currentRole = "student";
    private EditText etEmail, etPassword;
    private TextView tvRoleTitle, tvRoleSubtitle, tvRegister, tvSwitchRole;
    private LinearLayout layoutRegisterLink, chipQuickFill;
    private Button btnLogin;
    private ImageButton btnBackLogin;
    private HostelDbHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent().hasExtra("ROLE")) {
            currentRole = getIntent().getStringExtra("ROLE");
        }

        dbHelper = new HostelDbHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRoleUI();
    }

    private void initViews() {
        tvRoleTitle = findViewById(R.id.tvRoleTitle);
        tvRoleSubtitle = findViewById(R.id.tvRoleSubtitle);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBackLogin = findViewById(R.id.btnBackLogin);
        chipQuickFill = findViewById(R.id.chipQuickFill);
        layoutRegisterLink = findViewById(R.id.layoutRegisterLink);
        tvRegister = findViewById(R.id.tvRegister);
        tvSwitchRole = findViewById(R.id.tvSwitchRole);

        if (btnBackLogin != null) {
            btnBackLogin.setOnClickListener(v -> finish());
        }

        btnLogin.setOnClickListener(v -> performLogin());

        chipQuickFill.setOnClickListener(v -> {
            if ("admin".equalsIgnoreCase(currentRole)) {
                etEmail.setText("admin@hostel.com");
                etPassword.setText("admin123");
                Toast.makeText(LoginActivity.this, "Warden demo credentials applied", Toast.LENGTH_SHORT).show();
            } else {
                etEmail.setText("student@hostel.com");
                etPassword.setText("student123");
                Toast.makeText(LoginActivity.this, "Student demo credentials applied", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvSwitchRole.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RoleSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void setupRoleUI() {
        if ("admin".equalsIgnoreCase(currentRole)) {
            tvRoleTitle.setText("Hostel Warden Login");
            tvRoleSubtitle.setText("Authorized administrative access only");
            layoutRegisterLink.setVisibility(View.GONE);
            btnLogin.setBackgroundTintList(getColorStateList(R.color.primary_dark));
        } else {
            tvRoleTitle.setText("Student Portal Login");
            tvRoleSubtitle.setText("Enter your registered credentials to proceed");
            layoutRegisterLink.setVisibility(View.VISIBLE);
            btnLogin.setBackgroundTintList(getColorStateList(R.color.primary));
        }
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        User user = dbHelper.loginUser(email, password, currentRole);
        if (user != null) {
            sessionManager.createLoginSession(
                    user.getId(),
                    user.getRole(),
                    user.getName(),
                    user.getEmail(),
                    user.getRollNo(),
                    user.getRoomNumber()
            );

            Toast.makeText(this, "Welcome, " + user.getName(), Toast.LENGTH_SHORT).show();

            Intent intent;
            if ("admin".equalsIgnoreCase(user.getRole())) {
                intent = new Intent(LoginActivity.this, AdminMainActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, StudentMainActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password for " + currentRole + " role", Toast.LENGTH_LONG).show();
        }
    }
}

package com.hostel.manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class RoleSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_select);

        CardView cardStudent = findViewById(R.id.cardStudent);
        CardView cardAdmin = findViewById(R.id.cardAdmin);
        Button btnStudentLogin = findViewById(R.id.btnStudentLogin);
        Button btnAdminLogin = findViewById(R.id.btnAdminLogin);
        TextView btnRegisterDirect = findViewById(R.id.btnRegisterDirect);

        cardStudent.setOnClickListener(v -> openLogin("student"));
        btnStudentLogin.setOnClickListener(v -> openLogin("student"));
        if (btnRegisterDirect != null) {
            btnRegisterDirect.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        }

        cardAdmin.setOnClickListener(v -> openLogin("admin"));
        btnAdminLogin.setOnClickListener(v -> openLogin("admin"));

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new androidx.appcompat.app.AlertDialog.Builder(RoleSelectActivity.this)
                        .setTitle("Exit Hostel Manager")
                        .setMessage("Are you sure you want to exit the application?")
                        .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    private void openLogin(String role) {
        Intent intent = new Intent(RoleSelectActivity.this, LoginActivity.class);
        intent.putExtra("ROLE", role);
        startActivity(intent);
    }
}

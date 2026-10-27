package com.hostel.manager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddComplaintActivity extends AppCompatActivity {
    private Spinner spCategory;
    private EditText etRoomNumber, etTitle, etDescription;
    private RadioGroup rgUrgency;
    private Button btnSubmitComplaint, btnCancel;
    private HostelDbHelper dbHelper;
    private SessionManager sessionManager;

    private final String[] categories = {
            "Plumbing",
            "Electrical",
            "Wi-Fi & Internet",
            "Housekeeping / Cleaning",
            "Carpentry / Furniture",
            "Mess / Food Quality",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);

        dbHelper = new HostelDbHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
    }

    private void initViews() {
        spCategory = findViewById(R.id.spCategory);
        etRoomNumber = findViewById(R.id.etRoomNumber);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        rgUrgency = findViewById(R.id.rgUrgency);
        btnSubmitComplaint = findViewById(R.id.btnSubmitComplaint);
        btnCancel = findViewById(R.id.btnCancel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spCategory.setAdapter(adapter);

        // Auto-fill room number from session if available
        String savedRoom = sessionManager.getRoomNumber();
        if (savedRoom != null && !savedRoom.equals("Not Allocated")) {
            etRoomNumber.setText(savedRoom);
        }

        ImageButton btnBack = findViewById(R.id.btnBackComplaint);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnSubmitComplaint.setOnClickListener(v -> submitComplaint());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void submitComplaint() {
        String category = spCategory.getSelectedItem().toString();
        String roomNumber = etRoomNumber.getText().toString().trim();
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        int urgencyId = rgUrgency.getCheckedRadioButtonId();
        RadioButton rbUrgency = findViewById(urgencyId);
        String urgency = rbUrgency != null ? rbUrgency.getText().toString() : "Medium";

        if (TextUtils.isEmpty(roomNumber)) {
            etRoomNumber.setError("Room number is required");
            etRoomNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        Complaint complaint = new Complaint();
        complaint.setStudentId(sessionManager.getUserId());
        complaint.setStudentName(sessionManager.getName());
        complaint.setRoomNumber(roomNumber);
        complaint.setCategory(category);
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setUrgency(urgency);

        long id = dbHelper.addComplaint(complaint);
        if (id != -1) {
            Toast.makeText(this, "Complaint submitted successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to submit complaint. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}

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

public class AddRoomActivity extends AppCompatActivity {
    private EditText etRoomNumber, etFloor, etFeePerSem;
    private Spinner spBlock;
    private RadioGroup rgRoomType, rgCapacity;
    private Button btnAddRoomSubmit, btnCancelRoom;
    private HostelDbHelper dbHelper;

    private final String[] blocks = {
            "Block A (Boys)",
            "Block B (Boys)",
            "Block C (Girls)",
            "PG Scholars Wing"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        dbHelper = new HostelDbHelper(this);

        initViews();
    }

    private void initViews() {
        etRoomNumber = findViewById(R.id.etRoomNumber);
        etFloor = findViewById(R.id.etFloor);
        etFeePerSem = findViewById(R.id.etFeePerSem);
        spBlock = findViewById(R.id.spBlock);
        rgRoomType = findViewById(R.id.rgRoomType);
        rgCapacity = findViewById(R.id.rgCapacity);
        btnAddRoomSubmit = findViewById(R.id.btnAddRoomSubmit);
        btnCancelRoom = findViewById(R.id.btnCancelRoom);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, blocks);
        spBlock.setAdapter(adapter);

        ImageButton btnBack = findViewById(R.id.btnBackRoom);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnAddRoomSubmit.setOnClickListener(v -> saveRoom());
        btnCancelRoom.setOnClickListener(v -> finish());
    }

    private void saveRoom() {
        String roomNumber = etRoomNumber.getText().toString().trim();
        String block = spBlock.getSelectedItem().toString();
        String floorStr = etFloor.getText().toString().trim();
        String feeStr = etFeePerSem.getText().toString().trim();

        if (TextUtils.isEmpty(roomNumber)) {
            etRoomNumber.setError("Room number is required");
            etRoomNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(floorStr)) {
            etFloor.setError("Floor is required");
            etFloor.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(feeStr)) {
            etFeePerSem.setError("Semester fee is required");
            etFeePerSem.requestFocus();
            return;
        }

        int floor = Integer.parseInt(floorStr);
        double fee = Double.parseDouble(feeStr);

        int typeId = rgRoomType.getCheckedRadioButtonId();
        RadioButton rbType = findViewById(typeId);
        String roomType = rbType != null && rbType.getText().toString().contains("AC") ? "AC" : "Non-AC";

        int capId = rgCapacity.getCheckedRadioButtonId();
        int capacity = 2;
        if (capId == R.id.rbCap1) capacity = 1;
        else if (capId == R.id.rbCap3) capacity = 3;

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setBlock(block);
        room.setFloor(floor);
        room.setRoomType(roomType);
        room.setCapacity(capacity);
        room.setFeePerSem(fee);

        long id = dbHelper.addRoom(room);
        if (id != -1) {
            Toast.makeText(this, "Room " + roomNumber + " added successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Room number already exists or error saving.", Toast.LENGTH_LONG).show();
        }
    }
}

package com.hostel.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminMainActivity extends AppCompatActivity {
    private HostelDbHelper dbHelper;
    private SessionManager sessionManager;

    // Header Stats
    private TextView tvStatStudents, tvStatRooms, tvStatComplaints, tvStatFees;
    private ImageButton btnAdminLogout;

    // Sections
    private FrameLayout sectionRooms;
    private LinearLayout sectionComplaints, sectionFees, sectionStudents;

    // Bottom Navigation
    private LinearLayout adminNavRooms, adminNavComplaints, adminNavFees, adminNavStudents;
    private ImageView ivAdminNavRooms, ivAdminNavComplaints, ivAdminNavFees, ivAdminNavStudents;
    private TextView tvAdminNavRooms, tvAdminNavComplaints, tvAdminNavFees, tvAdminNavStudents;

    // Rooms
    private RecyclerView rvAdminRooms;
    private RoomAdapter roomAdapter;
    private ExtendedFloatingActionButton fabAddRoom;

    // Complaints
    private TextView tabAdminCompAll, tabAdminCompPending, tabAdminCompResolved;
    private RecyclerView rvAdminComplaints;
    private ComplaintAdapter complaintAdapter;
    private List<Complaint> allAdminComplaints = new ArrayList<>();
    private String adminCompFilter = "ALL";

    // Fees
    private RecyclerView rvAdminFees;
    private FeeAdapter feeAdapter;

    // Students
    private RecyclerView rvAdminStudents;
    private StudentAdapter studentAdapter;
    private int currentTabIndex = 0;

    private final ActivityResultLauncher<Intent> addRoomLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadRooms();
                    loadStats();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        dbHelper = new HostelDbHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupNavigation();
        loadAllData();

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentTabIndex != 0) {
                    switchTab(0);
                } else {
                    showExitDialog();
                }
            }
        });

        if (getIntent().hasExtra("TAB")) {
            switchTab(getIntent().getIntExtra("TAB", 0));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.hasExtra("TAB")) {
            switchTab(intent.getIntExtra("TAB", 0));
        }
    }

    private void initViews() {
        tvStatStudents = findViewById(R.id.tvStatStudents);
        tvStatRooms = findViewById(R.id.tvStatRooms);
        tvStatComplaints = findViewById(R.id.tvStatComplaints);
        tvStatFees = findViewById(R.id.tvStatFees);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        sectionRooms = findViewById(R.id.sectionRooms);
        sectionComplaints = findViewById(R.id.sectionComplaints);
        sectionFees = findViewById(R.id.sectionFees);
        sectionStudents = findViewById(R.id.sectionStudents);

        adminNavRooms = findViewById(R.id.adminNavRooms);
        adminNavComplaints = findViewById(R.id.adminNavComplaints);
        adminNavFees = findViewById(R.id.adminNavFees);
        adminNavStudents = findViewById(R.id.adminNavStudents);

        ivAdminNavRooms = findViewById(R.id.ivAdminNavRooms);
        ivAdminNavComplaints = findViewById(R.id.ivAdminNavComplaints);
        ivAdminNavFees = findViewById(R.id.ivAdminNavFees);
        ivAdminNavStudents = findViewById(R.id.ivAdminNavStudents);

        tvAdminNavRooms = findViewById(R.id.tvAdminNavRooms);
        tvAdminNavComplaints = findViewById(R.id.tvAdminNavComplaints);
        tvAdminNavFees = findViewById(R.id.tvAdminNavFees);
        tvAdminNavStudents = findViewById(R.id.tvAdminNavStudents);

        btnAdminLogout.setOnClickListener(v -> confirmLogout());

        // Rooms setup
        rvAdminRooms = findViewById(R.id.rvAdminRooms);
        fabAddRoom = findViewById(R.id.fabAddRoom);
        rvAdminRooms.setLayoutManager(new LinearLayoutManager(this));
        roomAdapter = new RoomAdapter(this, new ArrayList<>(), this::showRoomActionDialog);
        rvAdminRooms.setAdapter(roomAdapter);
        fabAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddRoomActivity.class);
            addRoomLauncher.launch(intent);
        });

        // Complaints setup
        tabAdminCompAll = findViewById(R.id.tabAdminCompAll);
        tabAdminCompPending = findViewById(R.id.tabAdminCompPending);
        tabAdminCompResolved = findViewById(R.id.tabAdminCompResolved);
        rvAdminComplaints = findViewById(R.id.rvAdminComplaints);
        rvAdminComplaints.setLayoutManager(new LinearLayoutManager(this));
        complaintAdapter = new ComplaintAdapter(this, new ArrayList<>(), this::showComplaintResolutionDialog);
        rvAdminComplaints.setAdapter(complaintAdapter);

        tabAdminCompAll.setOnClickListener(v -> filterAdminComplaints("ALL"));
        tabAdminCompPending.setOnClickListener(v -> filterAdminComplaints("PENDING"));
        tabAdminCompResolved.setOnClickListener(v -> filterAdminComplaints("RESOLVED"));

        // Fees setup
        rvAdminFees = findViewById(R.id.rvAdminFees);
        rvAdminFees.setLayoutManager(new LinearLayoutManager(this));
        feeAdapter = new FeeAdapter(this, new ArrayList<>(), this::showFeeActionDialog);
        rvAdminFees.setAdapter(feeAdapter);

        // Students setup
        rvAdminStudents = findViewById(R.id.rvAdminStudents);
        rvAdminStudents.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(this, new ArrayList<>(), this::showStudentActionDialog);
        rvAdminStudents.setAdapter(studentAdapter);
    }

    private void setupNavigation() {
        adminNavRooms.setOnClickListener(v -> switchTab(0));
        adminNavComplaints.setOnClickListener(v -> switchTab(1));
        adminNavFees.setOnClickListener(v -> switchTab(2));
        adminNavStudents.setOnClickListener(v -> switchTab(3));
    }

    private void switchTab(int index) {
        this.currentTabIndex = index;
        sectionRooms.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        sectionComplaints.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        sectionFees.setVisibility(index == 2 ? View.VISIBLE : View.GONE);
        sectionStudents.setVisibility(index == 3 ? View.VISIBLE : View.GONE);

        int activeColor = ContextCompat.getColor(this, R.color.primary);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_hint);

        ivAdminNavRooms.setColorFilter(index == 0 ? activeColor : inactiveColor);
        tvAdminNavRooms.setTextColor(index == 0 ? activeColor : inactiveColor);

        ivAdminNavComplaints.setColorFilter(index == 1 ? activeColor : inactiveColor);
        tvAdminNavComplaints.setTextColor(index == 1 ? activeColor : inactiveColor);

        ivAdminNavFees.setColorFilter(index == 2 ? activeColor : inactiveColor);
        tvAdminNavFees.setTextColor(index == 2 ? activeColor : inactiveColor);

        ivAdminNavStudents.setColorFilter(index == 3 ? activeColor : inactiveColor);
        tvAdminNavStudents.setTextColor(index == 3 ? activeColor : inactiveColor);
    }

    private void loadAllData() {
        loadStats();
        loadRooms();
        loadComplaints();
        loadFees();
        loadStudents();
    }

    private void loadStats() {
        int studentsCount = dbHelper.getTotalStudentsCount();
        int roomsCount = dbHelper.getTotalRoomsCount();
        int activeComp = dbHelper.getActiveComplaintsCount();
        double totalFees = dbHelper.getTotalFeesCollected();

        tvStatStudents.setText(String.valueOf(studentsCount));
        tvStatRooms.setText(String.valueOf(roomsCount));
        tvStatComplaints.setText(String.valueOf(activeComp));

        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        tvStatFees.setText(fmt.format(totalFees));
    }

    private void loadRooms() {
        List<Room> rooms = dbHelper.getAllRooms();
        roomAdapter.updateData(rooms);
    }

    private void loadComplaints() {
        allAdminComplaints = dbHelper.getAllComplaints();
        filterAdminComplaints(adminCompFilter);
    }

    private void filterAdminComplaints(String filter) {
        adminCompFilter = filter;
        tabAdminCompAll.setBackgroundResource(filter.equals("ALL") ? R.drawable.bg_badge_progress : R.drawable.bg_card_rounded);
        tabAdminCompAll.setTextColor(ContextCompat.getColor(this, filter.equals("ALL") ? R.color.primary : R.color.text_secondary));

        tabAdminCompPending.setBackgroundResource(filter.equals("PENDING") ? R.drawable.bg_badge_progress : R.drawable.bg_card_rounded);
        tabAdminCompPending.setTextColor(ContextCompat.getColor(this, filter.equals("PENDING") ? R.color.primary : R.color.text_secondary));

        tabAdminCompResolved.setBackgroundResource(filter.equals("RESOLVED") ? R.drawable.bg_badge_progress : R.drawable.bg_card_rounded);
        tabAdminCompResolved.setTextColor(ContextCompat.getColor(this, filter.equals("RESOLVED") ? R.color.primary : R.color.text_secondary));

        List<Complaint> filtered = new ArrayList<>();
        for (Complaint c : allAdminComplaints) {
            if (filter.equals("ALL")) filtered.add(c);
            else if (filter.equals("PENDING") && !"Resolved".equalsIgnoreCase(c.getStatus())) filtered.add(c);
            else if (filter.equals("RESOLVED") && "Resolved".equalsIgnoreCase(c.getStatus())) filtered.add(c);
        }
        complaintAdapter.updateData(filtered);
    }

    private void loadFees() {
        List<FeeRecord> fees = dbHelper.getAllFeeRecords();
        feeAdapter.updateData(fees);
    }

    private void loadStudents() {
        List<User> students = dbHelper.getAllStudents();
        studentAdapter.updateData(students);
    }

    private void showRoomActionDialog(Room room) {
        CharSequence[] options = new CharSequence[]{"Allocate Student to Room", "View Current Occupants", "Delete Room"};
        new AlertDialog.Builder(this)
                .setTitle("Room " + room.getRoomNumber() + " (" + room.getBlock() + ")")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAllocateStudentDialog(room);
                    } else if (which == 1) {
                        showRoomOccupantsDialog(room);
                    } else if (which == 2) {
                        confirmDeleteRoom(room);
                    }
                })
                .show();
    }

    private void showAllocateStudentDialog(Room room) {
        if (!room.isAvailable()) {
            Toast.makeText(this, "Room " + room.getRoomNumber() + " is already at maximum capacity!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<User> allStudents = dbHelper.getAllStudents();
        if (allStudents.isEmpty()) {
            Toast.makeText(this, "No registered students found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] studentNames = new String[allStudents.size()];
        for (int i = 0; i < allStudents.size(); i++) {
            User u = allStudents.get(i);
            studentNames[i] = u.getName() + " (" + u.getRollNo() + ") - Current: " + u.getRoomNumber();
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Student for Room " + room.getRoomNumber())
                .setItems(studentNames, (dialog, which) -> {
                    User selected = allStudents.get(which);
                    boolean ok = dbHelper.allocateStudentToRoom(selected.getId(), room.getId());
                    if (ok) {
                        Toast.makeText(this, "Allocated " + selected.getName() + " to Room " + room.getRoomNumber(), Toast.LENGTH_SHORT).show();
                        loadRooms();
                        loadStudents();
                        loadStats();
                    } else {
                        Toast.makeText(this, "Allocation failed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showRoomOccupantsDialog(Room room) {
        List<User> occupants = dbHelper.getRoommates(room.getId(), 0);
        StringBuilder sb = new StringBuilder();
        if (occupants.isEmpty()) {
            sb.append("This room is currently vacant.\nAvailable Beds: ").append(room.getCapacity());
        } else {
            sb.append("Current Residents (").append(occupants.size()).append("/").append(room.getCapacity()).append("):\n\n");
            for (User u : occupants) {
                sb.append("• ").append(u.getName()).append("\n  Roll: ").append(u.getRollNo()).append("\n  Phone: ").append(u.getPhone()).append("\n\n");
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Room " + room.getRoomNumber() + " Occupants")
                .setMessage(sb.toString().trim())
                .setPositiveButton("Close", null)
                .show();
    }

    private void confirmDeleteRoom(Room room) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Room")
                .setMessage("Are you sure you want to remove Room " + room.getRoomNumber() + "? Any assigned occupants will be deallocated.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteRoom(room.getId());
                    if (deleted) {
                        Toast.makeText(this, "Room deleted.", Toast.LENGTH_SHORT).show();
                        loadRooms();
                        loadStudents();
                        loadStats();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showComplaintResolutionDialog(Complaint comp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resolve Issue #" + comp.getId());

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_admin_complaint, null);
        builder.setView(v);

        TextView tvIssueInfo = v.findViewById(R.id.tvIssueInfo);
        RadioGroup rgStatus = v.findViewById(R.id.rgStatus);
        RadioButton rbPending = v.findViewById(R.id.rbPending);
        RadioButton rbInProgress = v.findViewById(R.id.rbInProgress);
        RadioButton rbResolved = v.findViewById(R.id.rbResolved);
        EditText etRemarks = v.findViewById(R.id.etRemarks);

        tvIssueInfo.setText("Student: " + comp.getStudentName() + " (Room " + comp.getRoomNumber() + ")\n" +
                "Issue: " + comp.getTitle() + " (" + comp.getCategory() + ")\n" +
                "Urgency: " + comp.getUrgency() + "\n\n" +
                "Details: " + comp.getDescription());

        if ("Resolved".equalsIgnoreCase(comp.getStatus())) rbResolved.setChecked(true);
        else if ("In Progress".equalsIgnoreCase(comp.getStatus())) rbInProgress.setChecked(true);
        else rbPending.setChecked(true);

        if (comp.getAdminRemarks() != null) etRemarks.setText(comp.getAdminRemarks());

        builder.setPositiveButton("Update Status", (dialog, which) -> {
            int selectedId = rgStatus.getCheckedRadioButtonId();
            String newStatus = "Pending";
            if (selectedId == R.id.rbInProgress) newStatus = "In Progress";
            else if (selectedId == R.id.rbResolved) newStatus = "Resolved";

            String remarks = etRemarks.getText().toString().trim();
            boolean ok = dbHelper.updateComplaintStatus(comp.getId(), newStatus, remarks);
            if (ok) {
                Toast.makeText(this, "Complaint status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                loadComplaints();
                loadStats();
            }
        });

        builder.setNegativeButton("Delete Issue", (dialog, which) -> {
            boolean del = dbHelper.deleteComplaint(comp.getId());
            if (del) {
                Toast.makeText(this, "Complaint deleted.", Toast.LENGTH_SHORT).show();
                loadComplaints();
                loadStats();
            }
        });

        builder.setNeutralButton("Cancel", null);
        builder.show();
    }

    private void showFeeActionDialog(FeeRecord fee) {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        new AlertDialog.Builder(this)
                .setTitle("Fee Record: " + fee.getStudentName())
                .setMessage("Roll No: " + fee.getRollNo() + "\nRoom: " + fee.getRoomNumber() +
                        "\nSemester: " + fee.getSemester() +
                        "\nTotal Due: " + fmt.format(fee.getTotalDue()) +
                        "\nPaid Amount: " + fmt.format(fee.getPaidAmount()) +
                        "\nBalance: " + fmt.format(fee.getBalanceDue()) +
                        "\nPayment Status: " + fee.getPaymentStatus() +
                        (fee.getTransactionId() != null && !fee.getTransactionId().isEmpty() ? "\nTxn ID: " + fee.getTransactionId() : ""))
                .setPositiveButton("Record Cash Payment", (dialog, which) -> {
                    if (fee.getBalanceDue() <= 0) {
                        Toast.makeText(this, "Fee is already fully settled!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String txn = "CASH-" + System.currentTimeMillis() % 100000;
                    String rec = "REC-ADMIN-" + (1000 + (int)(Math.random() * 9000));
                    boolean ok = dbHelper.recordPayment(fee.getId(), fee.getBalanceDue(), "Cash at Office", txn, rec);
                    if (ok) {
                        Toast.makeText(this, "Full balance recorded as paid via Cash!", Toast.LENGTH_LONG).show();
                        loadFees();
                        loadStats();
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void showStudentActionDialog(User student) {
        CharSequence[] options = new CharSequence[]{"Assign / Change Room", "Vacate / Deallocate Room", "View Student Profile"};
        new AlertDialog.Builder(this)
                .setTitle(student.getName() + " (" + student.getRollNo() + ")")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        List<Room> rooms = dbHelper.getAllRooms();
                        List<Room> avail = new ArrayList<>();
                        for (Room r : rooms) {
                            if (r.isAvailable()) avail.add(r);
                        }
                        if (avail.isEmpty()) {
                            Toast.makeText(this, "No vacant rooms available! Add a room first.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String[] roomTitles = new String[avail.size()];
                        for (int i = 0; i < avail.size(); i++) {
                            Room r = avail.get(i);
                            roomTitles[i] = "Room " + r.getRoomNumber() + " (" + r.getBlock() + ") - " + r.getAvailableBeds() + " beds free";
                        }
                        new AlertDialog.Builder(this)
                                .setTitle("Select Room for " + student.getName())
                                .setItems(roomTitles, (d, w) -> {
                                    Room picked = avail.get(w);
                                    boolean ok = dbHelper.allocateStudentToRoom(student.getId(), picked.getId());
                                    if (ok) {
                                        Toast.makeText(this, "Assigned " + student.getName() + " to Room " + picked.getRoomNumber(), Toast.LENGTH_SHORT).show();
                                        loadStudents();
                                        loadRooms();
                                        loadStats();
                                    }
                                })
                                .show();
                    } else if (which == 1) {
                        boolean ok = dbHelper.deallocateStudent(student.getId());
                        if (ok) {
                            Toast.makeText(this, student.getName() + " deallocated from room.", Toast.LENGTH_SHORT).show();
                            loadStudents();
                            loadRooms();
                            loadStats();
                        }
                    } else if (which == 2) {
                        new AlertDialog.Builder(this)
                                .setTitle(student.getName())
                                .setMessage("Roll No: " + student.getRollNo() +
                                        "\nEmail: " + student.getEmail() +
                                        "\nPhone: " + student.getPhone() +
                                        "\nGender: " + student.getGender() +
                                        "\nRoom: " + student.getRoomNumber() +
                                        "\nRegistered: " + student.getCreatedAt())
                                .setPositiveButton("Close", null)
                                .show();
                    }
                })
                .show();
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Warden Portal")
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Warden Logout")
                .setMessage("Are you sure you want to log out of Warden Portal?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Intent intent = new Intent(AdminMainActivity.this, RoleSelectActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

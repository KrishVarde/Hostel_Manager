package com.hostel.manager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import java.util.Random;

public class StudentMainActivity extends AppCompatActivity {
    private HostelDbHelper dbHelper;
    private SessionManager sessionManager;

    // Views
    private TextView tvWelcomeStudent, tvAllocatedRoomChip;
    private ImageButton btnLogout;

    // Tab content containers
    private ScrollView viewDashboard, viewMyRoom, viewFeesProfile;
    private LinearLayout viewComplaints;

    // Bottom Navigation items
    private LinearLayout navDashboard, navRoom, navComplaints, navFees;
    private ImageView ivNavDashboard, ivNavRoom, ivNavComplaints, ivNavFees;
    private TextView tvNavDashboard, tvNavRoom, tvNavComplaints, tvNavFees;

    // Dashboard widgets
    private TextView tvDashRoomNumber, tvDashRoomDetails, tvDashRoomAction;
    private TextView tvDashFeeDue, tvDashFeeStatusBadge;
    private TextView tvDashComplaintsSummary;
    private Button btnQuickRaiseComplaint, btnQuickPayFees;

    // My Room widgets
    private TextView tvMyRoomNumber, tvMyRoomBlockFloor, tvMyRoomType, tvMyRoomCapacity, tvRoommatesList;

    // Complaints widgets
    private TextView tabAllComplaints, tabPendingComplaints, tabResolvedComplaints, tvEmptyComplaints;
    private RecyclerView rvStudentComplaints;
    private ExtendedFloatingActionButton fabAddComplaint;
    private ComplaintAdapter complaintAdapter;
    private List<Complaint> allStudentComplaints = new ArrayList<>();
    private String currentComplaintFilter = "ALL";
    private int currentTabIndex = 0;

    // Fees & Profile widgets
    private TextView tvFeeBadge, tvFeeSemester, tvRentAmount, tvMessAmount, tvMaintAmount;
    private TextView tvTotalFeeAmount, tvPaidFeeAmount, tvBalanceAmount;
    private Button btnPayNow, btnViewReceipt, btnProfileLogout;
    private TextView tvProfileName, tvProfileDetails;

    private FeeRecord currentFeeRecord;

    private final ActivityResultLauncher<Intent> addComplaintLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadComplaints();
                    loadDashboardData();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

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
        tvWelcomeStudent = findViewById(R.id.tvWelcomeStudent);
        tvAllocatedRoomChip = findViewById(R.id.tvAllocatedRoomChip);
        btnLogout = findViewById(R.id.btnLogout);

        viewDashboard = findViewById(R.id.viewDashboard);
        viewMyRoom = findViewById(R.id.viewMyRoom);
        viewComplaints = findViewById(R.id.viewComplaints);
        viewFeesProfile = findViewById(R.id.viewFeesProfile);

        navDashboard = findViewById(R.id.navDashboard);
        navRoom = findViewById(R.id.navRoom);
        navComplaints = findViewById(R.id.navComplaints);
        navFees = findViewById(R.id.navFees);

        ivNavDashboard = findViewById(R.id.ivNavDashboard);
        ivNavRoom = findViewById(R.id.ivNavRoom);
        ivNavComplaints = findViewById(R.id.ivNavComplaints);
        ivNavFees = findViewById(R.id.ivNavFees);

        tvNavDashboard = findViewById(R.id.tvNavDashboard);
        tvNavRoom = findViewById(R.id.tvNavRoom);
        tvNavComplaints = findViewById(R.id.tvNavComplaints);
        tvNavFees = findViewById(R.id.tvNavFees);

        // Dashboard
        tvDashRoomNumber = findViewById(R.id.tvDashRoomNumber);
        tvDashRoomDetails = findViewById(R.id.tvDashRoomDetails);
        tvDashRoomAction = findViewById(R.id.tvDashRoomAction);
        tvDashFeeDue = findViewById(R.id.tvDashFeeDue);
        tvDashFeeStatusBadge = findViewById(R.id.tvDashFeeStatusBadge);
        tvDashComplaintsSummary = findViewById(R.id.tvDashComplaintsSummary);
        btnQuickRaiseComplaint = findViewById(R.id.btnQuickRaiseComplaint);
        btnQuickPayFees = findViewById(R.id.btnQuickPayFees);

        findViewById(R.id.cardDashRoom).setOnClickListener(v -> switchTab(1));
        findViewById(R.id.cardDashFee).setOnClickListener(v -> switchTab(3));
        findViewById(R.id.cardDashComplaints).setOnClickListener(v -> switchTab(2));
        btnQuickRaiseComplaint.setOnClickListener(v -> openAddComplaint());
        btnQuickPayFees.setOnClickListener(v -> openPaymentDialog());

        // My Room
        tvMyRoomNumber = findViewById(R.id.tvMyRoomNumber);
        tvMyRoomBlockFloor = findViewById(R.id.tvMyRoomBlockFloor);
        tvMyRoomType = findViewById(R.id.tvMyRoomType);
        tvMyRoomCapacity = findViewById(R.id.tvMyRoomCapacity);
        tvRoommatesList = findViewById(R.id.tvRoommatesList);

        // Complaints
        tabAllComplaints = findViewById(R.id.tabAllComplaints);
        tabPendingComplaints = findViewById(R.id.tabPendingComplaints);
        tabResolvedComplaints = findViewById(R.id.tabResolvedComplaints);
        rvStudentComplaints = findViewById(R.id.rvStudentComplaints);
        tvEmptyComplaints = findViewById(R.id.tvEmptyComplaints);
        fabAddComplaint = findViewById(R.id.fabAddComplaint);

        rvStudentComplaints.setLayoutManager(new LinearLayoutManager(this));
        complaintAdapter = new ComplaintAdapter(this, new ArrayList<>(), this::showComplaintDetailDialog);
        rvStudentComplaints.setAdapter(complaintAdapter);

        tabAllComplaints.setOnClickListener(v -> filterComplaints("ALL"));
        tabPendingComplaints.setOnClickListener(v -> filterComplaints("PENDING"));
        tabResolvedComplaints.setOnClickListener(v -> filterComplaints("RESOLVED"));
        fabAddComplaint.setOnClickListener(v -> openAddComplaint());

        // Fees
        tvFeeBadge = findViewById(R.id.tvFeeBadge);
        tvFeeSemester = findViewById(R.id.tvFeeSemester);
        tvRentAmount = findViewById(R.id.tvRentAmount);
        tvMessAmount = findViewById(R.id.tvMessAmount);
        tvMaintAmount = findViewById(R.id.tvMaintAmount);
        tvTotalFeeAmount = findViewById(R.id.tvTotalFeeAmount);
        tvPaidFeeAmount = findViewById(R.id.tvPaidFeeAmount);
        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        btnPayNow = findViewById(R.id.btnPayNow);
        btnViewReceipt = findViewById(R.id.btnViewReceipt);
        btnPayNow.setOnClickListener(v -> openPaymentDialog());
        btnViewReceipt.setOnClickListener(v -> showReceiptDialog());

        // Profile
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileDetails = findViewById(R.id.tvProfileDetails);
        btnProfileLogout = findViewById(R.id.btnProfileLogout);
        btnProfileLogout.setOnClickListener(v -> confirmLogout());
        btnLogout.setOnClickListener(v -> confirmLogout());
    }

    private void setupNavigation() {
        navDashboard.setOnClickListener(v -> switchTab(0));
        navRoom.setOnClickListener(v -> switchTab(1));
        navComplaints.setOnClickListener(v -> switchTab(2));
        navFees.setOnClickListener(v -> switchTab(3));
    }

    private void switchTab(int tabIndex) {
        this.currentTabIndex = tabIndex;
        viewDashboard.setVisibility(tabIndex == 0 ? View.VISIBLE : View.GONE);
        viewMyRoom.setVisibility(tabIndex == 1 ? View.VISIBLE : View.GONE);
        viewComplaints.setVisibility(tabIndex == 2 ? View.VISIBLE : View.GONE);
        viewFeesProfile.setVisibility(tabIndex == 3 ? View.VISIBLE : View.GONE);

        fabAddComplaint.setVisibility(tabIndex == 2 ? View.VISIBLE : View.GONE);

        int activeColor = ContextCompat.getColor(this, R.color.primary);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_hint);

        ivNavDashboard.setColorFilter(tabIndex == 0 ? activeColor : inactiveColor);
        tvNavDashboard.setTextColor(tabIndex == 0 ? activeColor : inactiveColor);

        ivNavRoom.setColorFilter(tabIndex == 1 ? activeColor : inactiveColor);
        tvNavRoom.setTextColor(tabIndex == 1 ? activeColor : inactiveColor);

        ivNavComplaints.setColorFilter(tabIndex == 2 ? activeColor : inactiveColor);
        tvNavComplaints.setTextColor(tabIndex == 2 ? activeColor : inactiveColor);

        ivNavFees.setColorFilter(tabIndex == 3 ? activeColor : inactiveColor);
        tvNavFees.setTextColor(tabIndex == 3 ? activeColor : inactiveColor);
    }

    private void loadAllData() {
        int studentId = sessionManager.getUserId();
        if (studentId <= 0) {
            studentId = 1;
        }
        User user = dbHelper.getUserById(studentId);

        if (user != null) {
            tvWelcomeStudent.setText("Welcome, " + user.getName().split(" ")[0] + "!");
            String roomTag = "Room: " + user.getRoomNumber() + " • " + user.getRollNo();
            tvAllocatedRoomChip.setText(roomTag);

            tvProfileName.setText(user.getName());
            tvProfileDetails.setText("Roll Number: " + user.getRollNo() +
                    "\nEmail: " + user.getEmail() +
                    "\nPhone: " + user.getPhone() +
                    "\nGender: " + (user.getGender() != null ? user.getGender() : "N/A") +
                    "\nAllocated Room: " + user.getRoomNumber());

            // Load Room Info
            if (user.getRoomId() > 0) {
                Room room = dbHelper.getRoomById(user.getRoomId());
                if (room != null) {
                    tvDashRoomNumber.setText("Room " + room.getRoomNumber());
                    tvDashRoomDetails.setText(room.getBlock() + " • Floor " + room.getFloor() + " • " + room.getRoomType());

                    tvMyRoomNumber.setText("Room " + room.getRoomNumber());
                    tvMyRoomBlockFloor.setText(room.getBlock() + " • Floor " + room.getFloor());
                    tvMyRoomType.setText(room.getRoomType() + " Room");
                    tvMyRoomCapacity.setText(room.getCapacity() + " Beds (" + room.getOccupied() + " Occupied)");

                    // Roommates
                    List<User> roommates = dbHelper.getRoommates(room.getId(), studentId);
                    if (roommates.isEmpty()) {
                        tvRoommatesList.setText("No other roommates allocated in this room.");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (User rm : roommates) {
                            sb.append("• ").append(rm.getName()).append(" (Roll: ").append(rm.getRollNo()).append(")\n  Contact: ").append(rm.getPhone()).append("\n\n");
                        }
                        tvRoommatesList.setText(sb.toString().trim());
                    }
                }
            } else {
                tvDashRoomNumber.setText("No Room Allocated");
                tvDashRoomDetails.setText("Contact the Warden office for room assignment.");
                tvMyRoomNumber.setText("Not Allocated");
                tvMyRoomBlockFloor.setText("Pending allocation by Warden.");
                tvMyRoomType.setText("N/A");
                tvMyRoomCapacity.setText("N/A");
                tvRoommatesList.setText("Please request room allocation from Warden.");
            }
        }

        loadDashboardData();
        loadComplaints();
        loadFees();
    }

    private void loadDashboardData() {
        int studentId = sessionManager.getUserId() > 0 ? sessionManager.getUserId() : 1;
        List<Complaint> studentComps = dbHelper.getComplaintsByStudent(studentId);
        int active = 0, resolved = 0;
        for (Complaint c : studentComps) {
            if ("Resolved".equalsIgnoreCase(c.getStatus())) resolved++;
            else active++;
        }
        tvDashComplaintsSummary.setText(active + " Active / " + resolved + " Resolved");

        FeeRecord fee = dbHelper.getFeeByStudent(studentId);
        if (fee != null) {
            NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            tvDashFeeDue.setText("Balance Due: " + fmt.format(fee.getBalanceDue()));
            tvDashFeeStatusBadge.setText(fee.getPaymentStatus());

            if ("Paid".equalsIgnoreCase(fee.getPaymentStatus())) {
                tvDashFeeStatusBadge.setBackgroundResource(R.drawable.bg_badge_paid);
                tvDashFeeStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.status_paid));
            } else if ("Partial".equalsIgnoreCase(fee.getPaymentStatus())) {
                tvDashFeeStatusBadge.setBackgroundResource(R.drawable.bg_badge_pending);
                tvDashFeeStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.status_pending));
            } else {
                tvDashFeeStatusBadge.setBackgroundResource(R.drawable.bg_badge_unpaid);
                tvDashFeeStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.status_unpaid));
            }
        }
    }

    private void loadComplaints() {
        int studentId = sessionManager.getUserId() > 0 ? sessionManager.getUserId() : 1;
        allStudentComplaints = dbHelper.getComplaintsByStudent(studentId);
        filterComplaints(currentComplaintFilter);
    }

    private void filterComplaints(String filter) {
        currentComplaintFilter = filter;
        tabAllComplaints.setBackgroundResource(filter.equals("ALL") ? R.drawable.bg_badge_progress : R.drawable.bg_card_rounded);
        tabAllComplaints.setTextColor(ContextCompat.getColor(this, filter.equals("ALL") ? R.color.primary : R.color.text_secondary));

        tabPendingComplaints.setBackgroundResource(filter.equals("PENDING") ? R.drawable.bg_badge_progress : R.drawable.bg_card_rounded);
        tabPendingComplaints.setTextColor(ContextCompat.getColor(this, filter.equals("PENDING") ? R.color.primary : R.color.text_secondary));

        tabResolvedComplaints.setBackgroundResource(filter.equals("RESOLVED") ? R.drawable.bg_badge_progress : R.drawable.bg_card_rounded);
        tabResolvedComplaints.setTextColor(ContextCompat.getColor(this, filter.equals("RESOLVED") ? R.color.primary : R.color.text_secondary));

        List<Complaint> filtered = new ArrayList<>();
        for (Complaint c : allStudentComplaints) {
            if (filter.equals("ALL")) {
                filtered.add(c);
            } else if (filter.equals("PENDING") && !"Resolved".equalsIgnoreCase(c.getStatus())) {
                filtered.add(c);
            } else if (filter.equals("RESOLVED") && "Resolved".equalsIgnoreCase(c.getStatus())) {
                filtered.add(c);
            }
        }
        complaintAdapter.updateData(filtered);
        if (filtered.isEmpty()) {
            rvStudentComplaints.setVisibility(View.GONE);
            if (tvEmptyComplaints != null) tvEmptyComplaints.setVisibility(View.VISIBLE);
        } else {
            rvStudentComplaints.setVisibility(View.VISIBLE);
            if (tvEmptyComplaints != null) tvEmptyComplaints.setVisibility(View.GONE);
        }
    }

    private void loadFees() {
        int studentId = sessionManager.getUserId() > 0 ? sessionManager.getUserId() : 1;
        currentFeeRecord = dbHelper.getFeeByStudent(studentId);

        if (currentFeeRecord != null) {
            NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            tvFeeSemester.setText(currentFeeRecord.getSemester());
            tvRentAmount.setText(fmt.format(currentFeeRecord.getRentAmount()));
            tvMessAmount.setText(fmt.format(currentFeeRecord.getMessAmount()));
            tvMaintAmount.setText(fmt.format(currentFeeRecord.getMaintenanceAmount()));
            tvTotalFeeAmount.setText(fmt.format(currentFeeRecord.getTotalDue()));
            tvPaidFeeAmount.setText(fmt.format(currentFeeRecord.getPaidAmount()));
            tvBalanceAmount.setText(fmt.format(currentFeeRecord.getBalanceDue()));

            String status = currentFeeRecord.getPaymentStatus();
            tvFeeBadge.setText(status);
            if ("Paid".equalsIgnoreCase(status)) {
                tvFeeBadge.setBackgroundResource(R.drawable.bg_badge_paid);
                tvFeeBadge.setTextColor(ContextCompat.getColor(this, R.color.status_paid));
                btnPayNow.setEnabled(false);
                btnPayNow.setText("Fully Paid ✓");
            } else {
                tvFeeBadge.setBackgroundResource("Partial".equalsIgnoreCase(status) ? R.drawable.bg_badge_pending : R.drawable.bg_badge_unpaid);
                tvFeeBadge.setTextColor(ContextCompat.getColor(this, "Partial".equalsIgnoreCase(status) ? R.color.status_pending : R.color.status_unpaid));
                btnPayNow.setEnabled(true);
                btnPayNow.setText("Pay Fees");
            }
        }
    }

    private void openAddComplaint() {
        Intent intent = new Intent(this, AddComplaintActivity.class);
        addComplaintLauncher.launch(intent);
    }

    private void showComplaintDetailDialog(Complaint complaint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(complaint.getTitle());

        StringBuilder sb = new StringBuilder();
        sb.append("Category: ").append(complaint.getCategory()).append("\n");
        sb.append("Room: ").append(complaint.getRoomNumber()).append("\n");
        sb.append("Urgency: ").append(complaint.getUrgency()).append("\n");
        sb.append("Status: ").append(complaint.getStatus()).append("\n");
        sb.append("Raised On: ").append(complaint.getCreatedAt()).append("\n\n");
        sb.append("Description:\n").append(complaint.getDescription()).append("\n\n");

        if (complaint.getAdminRemarks() != null && !complaint.getAdminRemarks().isEmpty()) {
            sb.append("Warden Remarks:\n").append(complaint.getAdminRemarks()).append("\n");
        }

        builder.setMessage(sb.toString());

        // Allow student to withdraw/delete complaint if still pending
        if ("Pending".equalsIgnoreCase(complaint.getStatus())) {
            builder.setNegativeButton("Withdraw Complaint", (dialog, which) -> {
                boolean deleted = dbHelper.deleteComplaint(complaint.getId());
                if (deleted) {
                    Toast.makeText(this, "Complaint withdrawn successfully.", Toast.LENGTH_SHORT).show();
                    loadComplaints();
                    loadDashboardData();
                }
            });
        }

        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private void openPaymentDialog() {
        if (currentFeeRecord == null || currentFeeRecord.getBalanceDue() <= 0) {
            Toast.makeText(this, "No outstanding fee balance!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_payment, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvDialogDue = dialogView.findViewById(R.id.tvDialogDue);
        EditText etPayAmount = dialogView.findViewById(R.id.etPayAmount);
        RadioGroup rgPaymentMode = dialogView.findViewById(R.id.rgPaymentMode);
        Button btnConfirmPay = dialogView.findViewById(R.id.btnConfirmPay);
        Button btnCancelPay = dialogView.findViewById(R.id.btnCancelPay);

        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        tvDialogDue.setText("Outstanding Balance: " + fmt.format(currentFeeRecord.getBalanceDue()));
        etPayAmount.setText(String.valueOf((int) currentFeeRecord.getBalanceDue()));

        btnConfirmPay.setOnClickListener(v -> {
            String amtStr = etPayAmount.getText().toString().trim();
            if (amtStr.isEmpty()) {
                etPayAmount.setError("Enter payment amount");
                return;
            }

            double amt = Double.parseDouble(amtStr);
            if (amt <= 0 || amt > currentFeeRecord.getBalanceDue()) {
                etPayAmount.setError("Amount must be between 1 and " + (int)currentFeeRecord.getBalanceDue());
                return;
            }

            int selectedModeId = rgPaymentMode.getCheckedRadioButtonId();
            RadioButton rb = dialogView.findViewById(selectedModeId);
            String method = rb != null ? rb.getText().toString() : "UPI";

            // Generate mock transaction ID & receipt
            int randTxn = 100000 + new Random().nextInt(900000);
            String txnId = "TXN" + System.currentTimeMillis() % 1000000 + randTxn;
            String receiptNo = "REC-2026-" + (1000 + new Random().nextInt(9000));

            boolean success = dbHelper.recordPayment(currentFeeRecord.getId(), amt, method, txnId, receiptNo);
            if (success) {
                dialog.dismiss();
                Toast.makeText(this, "Payment of " + fmt.format(amt) + " successful via " + method + "!", Toast.LENGTH_LONG).show();
                loadFees();
                loadDashboardData();
                showReceiptDialog();
            } else {
                Toast.makeText(this, "Payment failed. Please retry.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelPay.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showReceiptDialog() {
        if (currentFeeRecord == null || currentFeeRecord.getPaidAmount() <= 0) {
            Toast.makeText(this, "No payment records found to generate receipt.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Official Fee Payment Receipt");

        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        StringBuilder sb = new StringBuilder();
        sb.append("CAMPUS HOSTEL AUTHORITY\n");
        sb.append("Digital Payment Confirmation Receipt\n");
        sb.append("------------------------------------------\n");
        sb.append("Receipt No: ").append(currentFeeRecord.getReceiptNo().isEmpty() ? "REC-2026-DEFAULT" : currentFeeRecord.getReceiptNo()).append("\n");
        sb.append("Transaction ID: ").append(currentFeeRecord.getTransactionId().isEmpty() ? "TXN-DIRECT" : currentFeeRecord.getTransactionId()).append("\n");
        sb.append("Date: ").append(currentFeeRecord.getPaymentDate()).append("\n");
        sb.append("Payment Mode: ").append(currentFeeRecord.getPaymentMethod()).append("\n\n");
        sb.append("Student Name: ").append(currentFeeRecord.getStudentName()).append("\n");
        sb.append("Roll Number: ").append(currentFeeRecord.getRollNo()).append("\n");
        sb.append("Room Number: ").append(currentFeeRecord.getRoomNumber()).append("\n");
        sb.append("Semester: ").append(currentFeeRecord.getSemester()).append("\n\n");
        sb.append("Total Semester Fee: ").append(fmt.format(currentFeeRecord.getTotalDue())).append("\n");
        sb.append("Amount Paid: ").append(fmt.format(currentFeeRecord.getPaidAmount())).append("\n");
        sb.append("Balance Remaining: ").append(fmt.format(currentFeeRecord.getBalanceDue())).append("\n");
        sb.append("Status: ").append(currentFeeRecord.getPaymentStatus()).append("\n");
        sb.append("------------------------------------------\n");
        sb.append("✓ Verified Digital Receipt - Hostel Office\n");

        builder.setMessage(sb.toString());
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Hostel Manager")
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out of Hostel Manager?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Intent intent = new Intent(StudentMainActivity.this, RoleSelectActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

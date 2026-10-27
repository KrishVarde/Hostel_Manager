package com.hostel.manager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {
    private final Context context;
    private List<Complaint> complaintList;
    private final OnComplaintClickListener listener;

    public interface OnComplaintClickListener {
        void onComplaintClick(Complaint complaint);
    }

    public ComplaintAdapter(Context context, List<Complaint> complaintList, OnComplaintClickListener listener) {
        this.context = context;
        this.complaintList = complaintList;
        this.listener = listener;
    }

    public void updateData(List<Complaint> newList) {
        this.complaintList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Complaint c = complaintList.get(position);
        holder.tvCategory.setText(c.getCategory());
        holder.tvTitle.setText(c.getTitle());
        holder.tvDescription.setText(c.getDescription());
        holder.tvRoomNumber.setText("Room: " + c.getRoomNumber());
        holder.tvDate.setText(c.getCreatedAt());
        holder.tvUrgency.setText("Urgency: " + c.getUrgency());

        // Status badge styling
        String status = c.getStatus() != null ? c.getStatus() : "Pending";
        holder.tvStatus.setText(status);

        if ("Resolved".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_resolved);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_resolved));
        } else if ("In Progress".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_progress);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_in_progress));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_pending);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
        }

        // Urgency color
        if ("High".equalsIgnoreCase(c.getUrgency())) {
            holder.tvUrgency.setTextColor(ContextCompat.getColor(context, R.color.status_unpaid));
        } else if ("Medium".equalsIgnoreCase(c.getUrgency())) {
            holder.tvUrgency.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
        } else {
            holder.tvUrgency.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }

        // Remarks visibility
        if (c.getAdminRemarks() != null && !c.getAdminRemarks().trim().isEmpty()) {
            holder.layoutRemarks.setVisibility(View.VISIBLE);
            holder.tvAdminRemarks.setText(c.getAdminRemarks());
        } else {
            holder.layoutRemarks.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onComplaintClick(c);
        });
    }

    @Override
    public int getItemCount() {
        return complaintList != null ? complaintList.size() : 0;
    }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvStatus, tvTitle, tvDescription, tvRoomNumber, tvUrgency, tvDate, tvAdminRemarks;
        LinearLayout layoutRemarks;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvUrgency = itemView.findViewById(R.id.tvUrgency);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAdminRemarks = itemView.findViewById(R.id.tvAdminRemarks);
            layoutRemarks = itemView.findViewById(R.id.layoutRemarks);
        }
    }
}

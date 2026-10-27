package com.hostel.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FeeAdapter extends RecyclerView.Adapter<FeeAdapter.FeeViewHolder> {
    private final Context context;
    private List<FeeRecord> feeList;
    private final OnFeeClickListener listener;

    public interface OnFeeClickListener {
        void onFeeClick(FeeRecord fee);
    }

    public FeeAdapter(Context context, List<FeeRecord> feeList, OnFeeClickListener listener) {
        this.context = context;
        this.feeList = feeList;
        this.listener = listener;
    }

    public void updateData(List<FeeRecord> newList) {
        this.feeList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fee, parent, false);
        return new FeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeeViewHolder holder, int position) {
        FeeRecord f = feeList.get(position);
        holder.tvStudentName.setText(f.getStudentName());
        holder.tvRollRoom.setText("Roll: " + f.getRollNo() + " • Room: " + f.getRoomNumber());

        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        holder.tvTotalDue.setText(fmt.format(f.getTotalDue()));
        holder.tvPaidAmount.setText(fmt.format(f.getPaidAmount()));
        holder.tvBalance.setText(fmt.format(f.getBalanceDue()));

        String status = f.getPaymentStatus() != null ? f.getPaymentStatus() : "Unpaid";
        holder.tvStatus.setText(status);

        if ("Paid".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_paid);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_paid));
        } else if ("Partial".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_pending);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_unpaid);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_unpaid));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onFeeClick(f);
        });
    }

    @Override
    public int getItemCount() {
        return feeList != null ? feeList.size() : 0;
    }

    static class FeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvRollRoom, tvStatus, tvTotalDue, tvPaidAmount, tvBalance;

        public FeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvRollRoom = itemView.findViewById(R.id.tvRollRoom);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotalDue = itemView.findViewById(R.id.tvTotalDue);
            tvPaidAmount = itemView.findViewById(R.id.tvPaidAmount);
            tvBalance = itemView.findViewById(R.id.tvBalance);
        }
    }
}

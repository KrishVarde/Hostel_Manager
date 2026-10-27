package com.hostel.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private final Context context;
    private List<User> studentList;
    private final OnStudentClickListener listener;

    public interface OnStudentClickListener {
        void onStudentClick(User student);
    }

    public StudentAdapter(Context context, List<User> studentList, OnStudentClickListener listener) {
        this.context = context;
        this.studentList = studentList;
        this.listener = listener;
    }

    public void updateData(List<User> newList) {
        this.studentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        User u = studentList.get(position);
        holder.tvStudentName.setText(u.getName());
        holder.tvRollGender.setText("Roll No: " + u.getRollNo() + " • " + (u.getGender() != null ? u.getGender() : ""));
        holder.tvPhone.setText("📞 " + u.getPhone());
        holder.tvEmail.setText(u.getEmail());

        String room = u.getRoomNumber() != null ? u.getRoomNumber() : "Not Allocated";
        holder.tvRoomTag.setText("Room: " + room);

        if ("Not Allocated".equalsIgnoreCase(room)) {
            holder.tvRoomTag.setBackgroundResource(R.drawable.bg_badge_unpaid);
            holder.tvRoomTag.setTextColor(ContextCompat.getColor(context, R.color.status_unpaid));
        } else {
            holder.tvRoomTag.setBackgroundResource(R.drawable.bg_badge_progress);
            holder.tvRoomTag.setTextColor(ContextCompat.getColor(context, R.color.primary_light));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onStudentClick(u);
        });
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvRoomTag, tvRollGender, tvPhone, tvEmail;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvRoomTag = itemView.findViewById(R.id.tvRoomTag);
            tvRollGender = itemView.findViewById(R.id.tvRollGender);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
}

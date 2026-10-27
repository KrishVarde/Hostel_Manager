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

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private final Context context;
    private List<Room> roomList;
    private final OnRoomClickListener listener;

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    public RoomAdapter(Context context, List<Room> roomList, OnRoomClickListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }

    public void updateData(List<Room> newList) {
        this.roomList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room r = roomList.get(position);
        holder.tvRoomNumber.setText("Room " + r.getRoomNumber());
        holder.tvBlockFloor.setText(r.getBlock() + " • Floor " + r.getFloor());
        holder.tvRoomType.setText(r.getRoomType() + " Room");

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        holder.tvFee.setText(formatter.format(r.getFeePerSem()) + " / sem");

        String occupancy = r.getOccupied() + " / " + r.getCapacity() + " Beds";
        holder.tvOccupancyStatus.setText(occupancy);

        if (r.getOccupied() == 0) {
            holder.tvOccupancyStatus.setText("Vacant (" + r.getCapacity() + " Beds)");
            holder.tvOccupancyStatus.setBackgroundResource(R.drawable.bg_badge_resolved);
            holder.tvOccupancyStatus.setTextColor(ContextCompat.getColor(context, R.color.status_resolved));
        } else if (r.getOccupied() >= r.getCapacity()) {
            holder.tvOccupancyStatus.setText("Full (" + r.getCapacity() + "/" + r.getCapacity() + ")");
            holder.tvOccupancyStatus.setBackgroundResource(R.drawable.bg_badge_unpaid);
            holder.tvOccupancyStatus.setTextColor(ContextCompat.getColor(context, R.color.status_unpaid));
        } else {
            holder.tvOccupancyStatus.setText(r.getOccupied() + " / " + r.getCapacity() + " Occupied");
            holder.tvOccupancyStatus.setBackgroundResource(R.drawable.bg_badge_progress);
            holder.tvOccupancyStatus.setTextColor(ContextCompat.getColor(context, R.color.status_in_progress));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRoomClick(r);
        });
    }

    @Override
    public int getItemCount() {
        return roomList != null ? roomList.size() : 0;
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomNumber, tvOccupancyStatus, tvBlockFloor, tvRoomType, tvFee;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvOccupancyStatus = itemView.findViewById(R.id.tvOccupancyStatus);
            tvBlockFloor = itemView.findViewById(R.id.tvBlockFloor);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvFee = itemView.findViewById(R.id.tvFee);
        }
    }
}

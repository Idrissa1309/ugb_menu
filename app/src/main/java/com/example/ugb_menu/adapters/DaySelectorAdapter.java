package com.example.ugb_menu.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ugb_menu.R;
import com.example.ugb_menu.databinding.ItemDaySelectorBinding;
import java.util.List;

public class DaySelectorAdapter extends RecyclerView.Adapter<DaySelectorAdapter.ViewHolder> {

    private final List<String> days;
    private final List<String> dates;
    private int selectedPosition = 0;
    private final OnDaySelectedListener listener;

    public interface OnDaySelectedListener {
        void onDaySelected(int position);
    }

    public DaySelectorAdapter(List<String> days, List<String> dates, OnDaySelectedListener listener) {
        this.days = days;
        this.dates = dates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDaySelectorBinding binding = ItemDaySelectorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvDayName.setText(days.get(position));
        holder.binding.tvDayNumber.setText(dates.get(position));

        if (position == selectedPosition) {
            holder.binding.layoutDay.setBackgroundResource(R.drawable.bg_day_selected);
            holder.binding.tvDayName.setTextColor(Color.WHITE);
            holder.binding.tvDayNumber.setTextColor(Color.WHITE);
        } else {
            holder.binding.layoutDay.setBackground(null);
            holder.binding.tvDayName.setTextColor(Color.parseColor("#757575"));
            holder.binding.tvDayNumber.setTextColor(Color.parseColor("#212121"));
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onDaySelected(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemDaySelectorBinding binding;

        public ViewHolder(ItemDaySelectorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

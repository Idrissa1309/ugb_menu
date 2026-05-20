package com.example.ugb_menu.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ugb_menu.databinding.ItemMealBinding;
import com.example.ugb_menu.models.Meal;

import java.util.List;

public class AdminMealAdapter extends RecyclerView.Adapter<AdminMealAdapter.ViewHolder> {

    private final List<Meal> meals;
    private final OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public AdminMealAdapter(List<Meal> meals, OnMealClickListener listener) {
        this.meals = meals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMealBinding binding = ItemMealBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.binding.tvMealName.setText(meal.getName());
        holder.binding.tvMealType.setText(meal.getType());
        holder.binding.tvRestaurantName.setText(meal.getRestaurantName());

        if (meal.getImageUrl() != null && !meal.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(meal.getImageUrl()).into(holder.binding.ivMealImage);
        }

        holder.itemView.setOnClickListener(v -> listener.onMealClick(meal));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemMealBinding binding;

        public ViewHolder(ItemMealBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

package com.example.ugb_menu.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ugb_menu.MealDetailActivity;
import com.example.ugb_menu.R;
import com.example.ugb_menu.databinding.ItemFavoriteBinding;
import com.example.ugb_menu.models.Meal;
import com.example.ugb_menu.utils.FavoritesManager;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private final List<Meal> favoriteMeals;
    private final FavoritesManager favoritesManager;
    private final OnItemRemovedListener listener;

    public interface OnItemRemovedListener {
        void onItemRemoved();
    }

    public FavoritesAdapter(List<Meal> favoriteMeals, FavoritesManager favoritesManager, OnItemRemovedListener listener) {
        this.favoriteMeals = favoriteMeals;
        this.favoritesManager = favoritesManager;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteBinding binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = favoriteMeals.get(position);
        holder.binding.tvMealName.setText(meal.getName());
        holder.binding.tvRestaurantName.setText("Plat favori");

        Glide.with(holder.itemView.getContext())
                .load(meal.getImageUrl())
                .placeholder(R.drawable.logo_ugb)
                .centerCrop()
                .into(holder.binding.ivMealImage);

        holder.binding.btnRemoveFavorite.setOnClickListener(v -> {
            favoritesManager.removeFavorite(meal.getId());
            favoriteMeals.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favoriteMeals.size());
            if (listener != null) {
                listener.onItemRemoved();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MealDetailActivity.class);
            intent.putExtra(MealDetailActivity.EXTRA_MEAL, meal);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteMeals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemFavoriteBinding binding;

        public ViewHolder(ItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

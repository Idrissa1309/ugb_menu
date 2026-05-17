package com.example.ugb_menu.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ugb_menu.MealDetailActivity;
import com.example.ugb_menu.databinding.ItemMealBinding;
import com.example.ugb_menu.models.Meal;
import com.example.ugb_menu.utils.FavoritesManager;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    private final List<Meal> meals;
    private final FavoritesManager favoritesManager;
    private final OnFavoriteChangeListener listener;

    public interface OnFavoriteChangeListener {
        void onFavoriteChanged();
    }

    public MealAdapter(List<Meal> meals, FavoritesManager favoritesManager, OnFavoriteChangeListener listener) {
        this.meals = meals;
        this.favoritesManager = favoritesManager;
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

        boolean isFav = favoritesManager.isFavorite(meal.getId());
        holder.binding.ivFavorite.setImageResource(isFav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        holder.binding.ivFavorite.setOnClickListener(v -> {
            if (favoritesManager.isFavorite(meal.getId())) {
                favoritesManager.removeFavorite(meal.getId());
            } else {
                favoritesManager.addFavorite(meal.getId());
            }
            notifyItemChanged(position);
            if (listener != null) {
                listener.onFavoriteChanged();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MealDetailActivity.class);
            intent.putExtra(MealDetailActivity.EXTRA_MEAL, meal);
            holder.itemView.getContext().startActivity(intent);
        });

        Glide.with(holder.itemView.getContext())
                .load(meal.getImageUrl())
                .into(holder.binding.ivMealImage);
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

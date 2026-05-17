package com.example.ugb_menu.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ugb_menu.MealDetailActivity;
import com.example.ugb_menu.R;
import com.example.ugb_menu.databinding.ItemHomeRestoSummaryBinding;
import com.example.ugb_menu.models.DayMenu;
import com.example.ugb_menu.models.Meal;
import com.example.ugb_menu.models.Restaurant;
import java.util.List;

public class HomeRestoSummaryAdapter extends RecyclerView.Adapter<HomeRestoSummaryAdapter.ViewHolder> {

    private final List<Restaurant> restaurants;
    private List<Restaurant> filteredRestaurants;
    private int selectedDayIndex = 0;
    private String searchQuery = "";
    private String filterResto = "Tous";

    public HomeRestoSummaryAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        this.filteredRestaurants = new java.util.ArrayList<>(restaurants);
    }

    public void filter(String query, String resto) {
        this.searchQuery = query.toLowerCase();
        this.filterResto = resto;
        applyFilters();
    }

    private void applyFilters() {
        filteredRestaurants = new java.util.ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            if ("Tous".equals(filterResto) || restaurant.getName().equals(filterResto)) {
                // To properly filter by search query on meals, we might need a more complex logic
                // For now, let's just filter the list of restaurants if their name or any meal matches
                boolean matches = restaurant.getName().toLowerCase().contains(searchQuery);
                
                if (!matches && selectedDayIndex < restaurant.getWeeklyMenu().size()) {
                    for (com.example.ugb_menu.models.Meal meal : restaurant.getWeeklyMenu().get(selectedDayIndex).getMeals()) {
                        if (meal.getName().toLowerCase().contains(searchQuery)) {
                            matches = true;
                            break;
                        }
                    }
                }

                if (matches) {
                    filteredRestaurants.add(restaurant);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setSelectedDayIndex(int index) {
        this.selectedDayIndex = index;
        applyFilters();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeRestoSummaryBinding binding = ItemHomeRestoSummaryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = filteredRestaurants.get(position);
        holder.binding.tvRestoName.setText(restaurant.getName());

        // Dynamic colors for Resto icons
        int restoColor = Color.parseColor("#008947"); // Default Green
        if ("Resto 2".equals(restaurant.getName())) {
            restoColor = Color.parseColor("#0277BD"); // Blue for Resto 2
        }
        
        holder.binding.tvRestoName.setTextColor(restoColor);
        GradientDrawable background = (GradientDrawable) holder.binding.ivRestoIcon.getBackground();
        background.setColor(restoColor);

        if (restaurant.getWeeklyMenu() != null && selectedDayIndex < restaurant.getWeeklyMenu().size()) {
            DayMenu selectedDay = restaurant.getWeeklyMenu().get(selectedDayIndex);
            List<Meal> meals = selectedDay.getMeals();
            
            boolean hasLunch = false;
            boolean hasDinner = false;

            for (Meal meal : meals) {
                // Ensure meal has contextual info
                meal.setRestaurantName(restaurant.getName());
                meal.setDate(selectedDay.getDate());

                if ("Déjeuner".equals(meal.getType())) {
                    hasLunch = true;
                    holder.binding.tvLunchName.setText(meal.getName());
                    Glide.with(holder.itemView.getContext())
                            .load(meal.getImageUrl())
                            .placeholder(R.drawable.logo_ugb)
                            .centerCrop()
                            .into(holder.binding.ivLunchImage);
                    
                    holder.binding.layoutLunch.setOnClickListener(v -> navigateToDetail(holder, meal, meals));
                } else if ("Dîner".equals(meal.getType())) {
                    hasDinner = true;
                    holder.binding.tvDinnerName.setText(meal.getName());
                    Glide.with(holder.itemView.getContext())
                            .load(meal.getImageUrl())
                            .placeholder(R.drawable.logo_ugb)
                            .centerCrop()
                            .into(holder.binding.ivDinnerImage);

                    holder.binding.layoutDinner.setOnClickListener(v -> navigateToDetail(holder, meal, meals));
                }
            }

            holder.binding.layoutLunch.setVisibility(hasLunch ? View.VISIBLE : View.GONE);
            holder.binding.layoutDinner.setVisibility(hasDinner ? View.VISIBLE : View.GONE);
            holder.binding.divider.setVisibility(hasLunch && hasDinner ? View.VISIBLE : View.GONE);
        }
    }

    private void navigateToDetail(ViewHolder holder, Meal meal, List<Meal> allMeals) {
        Intent intent = new Intent(holder.itemView.getContext(), MealDetailActivity.class);
        intent.putExtra(MealDetailActivity.EXTRA_MEAL, meal);
        intent.putExtra("extra_todays_meals", new java.util.ArrayList<>(allMeals));
        holder.itemView.getContext().startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return filteredRestaurants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemHomeRestoSummaryBinding binding;

        public ViewHolder(ItemHomeRestoSummaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

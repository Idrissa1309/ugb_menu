package com.example.ugb_menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.ugb_menu.adapters.FavoritesAdapter;
import com.example.ugb_menu.databinding.FragmentFavoritesBinding;
import com.example.ugb_menu.models.Meal;
import com.example.ugb_menu.models.Restaurant;
import com.example.ugb_menu.repository.MenuRepository;
import com.example.ugb_menu.utils.FavoritesManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private FavoritesManager favoritesManager;
    private MenuRepository menuRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favoritesManager = new FavoritesManager(requireContext());
        menuRepository = new MenuRepository(requireContext());
        
        // Handle window insets to avoid status bar/taskbar overflow
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.btnDeleteAll.setOnClickListener(v -> clearAllFavorites());
        
        loadFavorites();
    }

    private void clearAllFavorites() {
        Set<String> ids = favoritesManager.getFavoriteIds();
        for (String id : ids) {
            favoritesManager.removeFavorite(id);
        }
        loadFavorites();
    }

    private void loadFavorites() {
        Set<String> favoriteIds = favoritesManager.getFavoriteIds();
        List<Restaurant> restaurants = menuRepository.getRestaurants();
        List<Meal> favoriteMeals = new ArrayList<>();

        for (Restaurant resto : restaurants) {
            if (resto.getWeeklyMenu() != null) {
                for (var dayMenu : resto.getWeeklyMenu()) {
                    for (Meal meal : dayMenu.getMeals()) {
                        if (favoriteIds.contains(meal.getId())) {
                            favoriteMeals.add(meal);
                        }
                    }
                }
            }
        }

        if (favoriteMeals.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.rvFavorites.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.rvFavorites.setVisibility(View.VISIBLE);
            FavoritesAdapter adapter = new FavoritesAdapter(favoriteMeals, favoritesManager, this::checkEmptyState);
            binding.rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvFavorites.setAdapter(adapter);
        }
    }
    
    private void checkEmptyState() {
        if (favoritesManager.getFavoriteIds().isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.rvFavorites.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites(); // Refresh when coming back from Detail screen
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

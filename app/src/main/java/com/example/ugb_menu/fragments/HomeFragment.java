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
import com.example.ugb_menu.adapters.HomeRestoSummaryAdapter;
import com.example.ugb_menu.databinding.FragmentHomeBinding;
import androidx.lifecycle.ViewModelProvider;
import com.example.ugb_menu.viewmodels.MenuViewModel;
import com.example.ugb_menu.models.Restaurant;
import com.example.ugb_menu.repository.MenuRepository;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MenuViewModel menuViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        
        // Handle window insets to avoid taskbar overflow
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0); // Top padding for status bar
            return insets;
        });

        updateDateUI();
        observeViewModel();
    }

    private void observeViewModel() {
        menuViewModel.getIsLoading().observe(getViewLifecycleOwner(), this::showLoading);

        menuViewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            if (restaurants == null || restaurants.isEmpty()) {
                binding.rvTodaySummary.setVisibility(View.GONE);
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String todayStr = sdf.format(new Date());

            int index = MenuRepository.getDayIndexForDate(todayStr, restaurants);

            if (index != -1) {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.rvTodaySummary.setVisibility(View.VISIBLE);
                HomeRestoSummaryAdapter adapter = new HomeRestoSummaryAdapter(restaurants);
                adapter.setSelectedDayIndex(index);
                binding.rvTodaySummary.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.rvTodaySummary.setAdapter(adapter);
            } else {
                // Fallback to first day if today's menu is not available
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.rvTodaySummary.setVisibility(View.VISIBLE);
                HomeRestoSummaryAdapter adapter = new HomeRestoSummaryAdapter(restaurants);
                adapter.setSelectedDayIndex(0);
                binding.rvTodaySummary.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.rvTodaySummary.setAdapter(adapter);
            }
        });

        menuViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                android.widget.Toast.makeText(requireContext(), error, android.widget.Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateDateUI() {
        Calendar calendar = Calendar.getInstance();

        // Format for display: "Lundi 20 Mai 2024"
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH);
        String formattedDate = displayFormat.format(calendar.getTime());
        
        // Capitalize first letter
        if (!formattedDate.isEmpty()) {
            formattedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
        }
        binding.tvDate.setText(formattedDate);

        // Week number
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        binding.tvWeek.setText("Semaine " + weekOfYear);
    }

    private void showLoading(boolean isLoading) {
        if (binding == null) return;
        binding.layoutShimmer.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.rvTodaySummary.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

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
import com.example.ugb_menu.adapters.DaySelectorAdapter;
import com.example.ugb_menu.adapters.HomeRestoSummaryAdapter;
import com.example.ugb_menu.databinding.FragmentMenusBinding;
import androidx.lifecycle.ViewModelProvider;
import com.example.ugb_menu.viewmodels.MenuViewModel;
import com.example.ugb_menu.models.Restaurant;
import com.example.ugb_menu.repository.MenuRepository;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.chip.Chip;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MenusFragment extends Fragment {

    private FragmentMenusBinding binding;
    private List<Restaurant> restaurants;
    private HomeRestoSummaryAdapter menuAdapter;
    private MenuViewModel menuViewModel;
    private List<String> weekDates;
    private List<String> weekDayNames;
    private List<String> weekFullDates; // yyyy-MM-dd format

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMenusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        
        menuViewModel.getRestaurants().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                restaurants = list;
                setupMenuRecyclerView();
                setupDaySelector();
                setupSearchAndFilters();
            }
        });

        menuViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Optional: show loading in MenusFragment if needed
        });

        menuViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                android.widget.Toast.makeText(requireContext(), error, android.widget.Toast.LENGTH_LONG).show();
            }
        });

        // Handle window insets to avoid status bar overflow
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupSearchAndFilters() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupFilters.setOnCheckedChangeListener((group, checkedId) -> updateFilter());
    }

    private void updateFilter() {
        String query = binding.etSearch.getText().toString();
        String resto = "Tous";
        
        int checkedChipId = binding.chipGroupFilters.getCheckedChipId();
        if (checkedChipId != View.NO_ID) {
            Chip chip = binding.getRoot().findViewById(checkedChipId);
            if (chip != null) {
                resto = chip.getText().toString();
            }
        }
        
        if (menuAdapter != null) {
            menuAdapter.filter(query, resto);
        }
    }

    private void setupDaySelector() {
        calculateWeekDates();
        
        DaySelectorAdapter adapter = new DaySelectorAdapter(weekDayNames, weekDates, position -> {
            if (menuAdapter != null && restaurants != null) {
                String selectedDate = weekFullDates.get(position);
                int index = MenuRepository.getDayIndexForDate(selectedDate, restaurants);
                
                if (index != -1) {
                    binding.rvWeeklyMenu.setVisibility(View.VISIBLE);
                    binding.layoutEmptyState.setVisibility(View.GONE);
                    menuAdapter.setSelectedDayIndex(index);
                } else {
                    binding.rvWeeklyMenu.setVisibility(View.GONE);
                    binding.layoutEmptyState.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.rvDaySelector.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvDaySelector.setAdapter(adapter);

        // Select current day if possible
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());
        int todayIndex = weekFullDates.indexOf(today);
        if (todayIndex != -1) {
            adapter.setSelectedPosition(todayIndex);
            // The listener won't trigger automatically on setSelectedPosition, so we trigger manual update
            int menuIndex = MenuRepository.getDayIndexForDate(today, restaurants);
            if (menuIndex != -1 && menuAdapter != null) {
                menuAdapter.setSelectedDayIndex(menuIndex);
            }
        }
    }

    private void calculateWeekDates() {
        weekDates = new ArrayList<>();
        weekDayNames = new ArrayList<>();
        weekFullDates = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEE", Locale.FRENCH);
        SimpleDateFormat dayNumFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String dayName = dayNameFormat.format(cal.getTime());
            // Capitalize and remove dot
            dayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1).replace(".", "");
            
            weekDayNames.add(dayName);
            weekDates.add(dayNumFormat.format(cal.getTime()));
            weekFullDates.add(fullDateFormat.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void setupMenuRecyclerView() {
        if (restaurants == null) return;
        menuAdapter = new HomeRestoSummaryAdapter(restaurants);
        binding.rvWeeklyMenu.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvWeeklyMenu.setAdapter(menuAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

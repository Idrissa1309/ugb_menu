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
import com.example.ugb_menu.models.Restaurant;
import com.example.ugb_menu.repository.MenuRepository;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.chip.Chip;
import java.util.Arrays;
import java.util.List;

public class MenusFragment extends Fragment {

    private FragmentMenusBinding binding;
    private List<Restaurant> restaurants;
    private HomeRestoSummaryAdapter menuAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMenusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuRepository menuRepository = new MenuRepository(requireContext());
        
        menuRepository.getMenuAsync(list -> {
            restaurants = list;
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    setupDaySelector();
                    setupMenuRecyclerView();
                    setupSearchAndFilters();
                });
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
        List<String> dayNames = Arrays.asList("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim");
        List<String> dayNumbers = Arrays.asList("04", "05", "06", "07", "08", "09", "10");

        DaySelectorAdapter adapter = new DaySelectorAdapter(dayNames, dayNumbers, position -> {
            if (menuAdapter != null) {
                menuAdapter.setSelectedDayIndex(position);
            }
        });

        binding.rvDaySelector.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvDaySelector.setAdapter(adapter);
    }

    private void setupMenuRecyclerView() {
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

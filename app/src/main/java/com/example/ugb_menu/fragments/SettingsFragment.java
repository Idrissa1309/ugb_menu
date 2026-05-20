package com.example.ugb_menu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.ugb_menu.admin.AdminLoginActivity;
import com.example.ugb_menu.databinding.FragmentSettingsBinding;
import com.example.ugb_menu.utils.ThemeManager;
import com.example.ugb_menu.R;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private ThemeManager themeManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        themeManager = new ThemeManager(requireContext());

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        setupListeners();
    }

    private void setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = isChecked ? "Notifications activées" : "Notifications désactivées";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });

        binding.btnAbout.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "UGB Menu v1.0.0\nDéveloppé pour les étudiants", Toast.LENGTH_LONG).show()
        );

        binding.btnContact.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Redirection vers le support du CROUS...", Toast.LENGTH_SHORT).show()
        );

        binding.btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AdminLoginActivity.class);
            startActivity(intent);
        });

        setupThemeSelector();
    }

    private void setupThemeSelector() {
        String currentTheme = themeManager.getTheme();
        switch (currentTheme) {
            case ThemeManager.THEME_BLUE:
                binding.chipThemeBlue.setChecked(true);
                break;
            case ThemeManager.THEME_BLACK:
                binding.chipThemeBlack.setChecked(true);
                break;
            case ThemeManager.THEME_MAROON:
                binding.chipThemeMaroon.setChecked(true);
                break;
            default:
                binding.chipThemeGreen.setChecked(true);
                break;
        }

        binding.chipGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedTheme = ThemeManager.THEME_GREEN;
            if (checkedId == R.id.chip_theme_blue) selectedTheme = ThemeManager.THEME_BLUE;
            else if (checkedId == R.id.chip_theme_black) selectedTheme = ThemeManager.THEME_BLACK;
            else if (checkedId == R.id.chip_theme_maroon) selectedTheme = ThemeManager.THEME_MAROON;

            if (!selectedTheme.equals(themeManager.getTheme())) {
                themeManager.setTheme(selectedTheme);
                // Restart activity to apply theme
                requireActivity().recreate();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.example.ugb_menu.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.ugb_menu.databinding.FragmentNotificationsBinding;
import com.example.ugb_menu.models.DayMenu;
import com.example.ugb_menu.models.Meal;
import com.example.ugb_menu.models.Restaurant;
import com.example.ugb_menu.repository.MenuRepository;
import com.example.ugb_menu.utils.NotificationHelper;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private MenuRepository menuRepository;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    sendTestNotification();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuRepository = new MenuRepository(requireContext());

        binding.btnTestNotification.setOnClickListener(v -> checkPermissionAndNotify());
        binding.btnSend.setOnClickListener(v -> handleAIQuery());
    }

    private void checkPermissionAndNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                sendTestNotification();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            sendTestNotification();
        }
    }

    private void sendTestNotification() {
        NotificationHelper.showMealNotification(requireContext(), "Alerte Repas", "Le déjeuner est servi à Resto 1 : Riz au poulet !");
    }

    private void handleAIQuery() {
        String query = binding.etQuery.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) return;

        binding.tvChatHistory.append("\n\nVous: " + query);
        binding.etQuery.setText("");

        String response = getAIResponse(query);
        binding.tvChatHistory.append("\nAssistant: " + response);

        binding.scrollChat.post(() -> binding.scrollChat.fullScroll(View.FOCUS_DOWN));
    }

    private String getAIResponse(String query) {
        List<Restaurant> restaurants = menuRepository.getRestaurants();

        if (query.contains("resto 1") || query.contains("restaurant 1")) {
            return getRestoSummary(restaurants.get(0));
        } else if (query.contains("resto 2") || query.contains("restaurant 2")) {
            return getRestoSummary(restaurants.get(1));
        } else if (query.contains("menu") || query.contains("mange")) {
            StringBuilder sb = new StringBuilder("Voici le menu du jour :");
            for (Restaurant r : restaurants) {
                sb.append("\n- ").append(r.getName()).append(": ").append(getShortSummary(r));
            }
            return sb.toString();
        }

        return "Désolé, je ne comprends pas votre question. Essayez de demander 'Menu de Resto 1' ou 'Qu'est-ce qu'on mange ?'";
    }

    private String getRestoSummary(Restaurant restaurant) {
        if (restaurant.getWeeklyMenu() == null || restaurant.getWeeklyMenu().isEmpty()) return "Pas de menu disponible pour " + restaurant.getName();
        DayMenu today = restaurant.getWeeklyMenu().get(0);
        StringBuilder sb = new StringBuilder("Aujourd'hui à " + restaurant.getName() + " :");
        for (Meal meal : today.getMeals()) {
            sb.append("\n- ").append(meal.getType()).append(": ").append(meal.getName());
        }
        return sb.toString();
    }

    private String getShortSummary(Restaurant restaurant) {
        if (restaurant.getWeeklyMenu() == null || restaurant.getWeeklyMenu().isEmpty()) return "N/A";
        DayMenu today = restaurant.getWeeklyMenu().get(0);
        if (today.getMeals().isEmpty()) return "N/A";
        return today.getMeals().get(0).getName();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

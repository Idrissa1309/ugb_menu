package com.example.ugb_menu;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.ugb_menu.fragments.FavoritesFragment;
import com.example.ugb_menu.fragments.HomeFragment;
import com.example.ugb_menu.fragments.MenusFragment;
import com.example.ugb_menu.fragments.SettingsFragment;
import com.example.ugb_menu.utils.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.ugb_menu.utils.MealAlertWorker;
import java.util.concurrent.TimeUnit;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ThemeManager(this).applyTheme(this);
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_root), (v, insets) -> {
            androidx.core.graphics.Insets navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            
            // We apply 0 padding to the root to let the BottomNavigationView 
            // handle its own insets naturally, or we force it if needed.
            v.setPadding(0, 0, 0, 0);
            
            bottomNav.setPadding(0, 0, 0, navigationBars.bottom);
            return insets;
        });

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_menus) {
                selectedFragment = new MenusFragment();
            } else if (itemId == R.id.nav_favorites) {
                selectedFragment = new FavoritesFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        scheduleMealAlerts();
    }

    private void scheduleMealAlerts() {
        PeriodicWorkRequest alertRequest =
                new PeriodicWorkRequest.Builder(MealAlertWorker.class, 24, TimeUnit.HOURS)
                        .setInitialDelay(8, TimeUnit.HOURS) // Example: run after 8 hours (morning)
                        .build();

        WorkManager.getInstance(this).enqueue(alertRequest);
    }
}

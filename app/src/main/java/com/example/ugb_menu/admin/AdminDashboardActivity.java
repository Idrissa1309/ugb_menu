package com.example.ugb_menu.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ugb_menu.databinding.ActivityAdminDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private String role;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        role = getIntent().getStringExtra("ADMIN_ROLE");
        restaurantId = getIntent().getStringExtra("RESTO_ID");

        if ("manager".equals(role)) {
            binding.cardRestaurants.setVisibility(android.view.View.GONE);
            binding.cardNotifications.setVisibility(android.view.View.GONE);
        }

        binding.cardManageMenus.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageMenusActivity.class);
            intent.putExtra("RESTO_ID", restaurantId);
            startActivity(intent);
        });

        binding.cardModeration.setOnClickListener(v -> {
            startActivity(new Intent(this, ReviewModerationActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
        });
    }
}

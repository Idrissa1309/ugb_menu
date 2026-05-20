package com.example.ugb_menu.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.ugb_menu.adapters.AdminMealAdapter;
import com.example.ugb_menu.databinding.ActivityManageMenusBinding;
import com.example.ugb_menu.models.Meal;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageMenusActivity extends AppCompatActivity {

    private ActivityManageMenusBinding binding;
    private FirebaseFirestore db;
    private AdminMealAdapter mealAdapter;
    private List<Meal> mealList = new ArrayList<>();
    private String selectedRestoId = "resto_1"; // Default or from role
    private String selectedDayId = "2026-05-19"; // Example date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageMenusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        setupSpinner();
        setupRecyclerView();

        binding.fabAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditMealActivity.class);
            intent.putExtra("RESTO_ID", selectedRestoId);
            intent.putExtra("DAY_ID", selectedDayId);
            startActivity(intent);
        });
    }

    private void setupSpinner() {
        String[] restaurants = {"Resto 1", "Resto 2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, restaurants);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRestaurants.setAdapter(adapter);

        binding.spinnerRestaurants.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRestoId = (position == 0) ? "resto_1" : "resto_2";
                loadMeals();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        mealAdapter = new AdminMealAdapter(mealList, meal -> {
            Intent intent = new Intent(this, EditMealActivity.class);
            intent.putExtra("RESTO_ID", selectedRestoId);
            intent.putExtra("DAY_ID", selectedDayId);
            intent.putExtra("MEAL", meal);
            startActivity(intent);
        });
        binding.rvMeals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMeals.setAdapter(mealAdapter);
    }

    private void loadMeals() {
        db.collection("restaurants").document(selectedRestoId)
                .collection("weeklyMenu").document(selectedDayId)
                .collection("meals")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mealList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mealList.add(document.toObject(Meal.class));
                        }
                        mealAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMeals();
    }
}

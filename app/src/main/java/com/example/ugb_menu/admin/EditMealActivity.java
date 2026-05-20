package com.example.ugb_menu.admin;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.ugb_menu.R;
import com.bumptech.glide.Glide;
import com.example.ugb_menu.R;
import com.example.ugb_menu.databinding.ActivityEditMealBinding;
import com.example.ugb_menu.models.Meal;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class EditMealActivity extends AppCompatActivity {

    private ActivityEditMealBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri imageUri;
    private String restaurantId;
    private String dayId;
    private Meal mealToEdit;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    binding.ivMealImage.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        binding = ActivityEditMealBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        restaurantId = getIntent().getStringExtra("RESTO_ID");
        dayId = getIntent().getStringExtra("DAY_ID");
        mealToEdit = (Meal) getIntent().getSerializableExtra("MEAL");

        if (mealToEdit != null) {
            populateFields(mealToEdit);
        }

        binding.btnPickImage.setOnClickListener(v -> mGetContent.launch("image/*"));
        binding.btnSaveMeal.setOnClickListener(v -> saveMeal());
    }

    private void populateFields(Meal meal) {
        binding.etMealName.setText(meal.getName());
        binding.etMealDescription.setText(meal.getDescription());
        if ("Petit Déj".equals(meal.getType())) binding.rbBreakfast.setChecked(true);
        else if ("Déjeuner".equals(meal.getType())) binding.rbLunch.setChecked(true);
        else if ("Dîner".equals(meal.getType())) binding.rbDinner.setChecked(true);
        
        if (meal.getImageUrl() != null && !meal.getImageUrl().isEmpty()) {
            Glide.with(this).load(meal.getImageUrl()).into(binding.ivMealImage);
        }
    }

    private void saveMeal() {
        String name = binding.etMealName.getText().toString().trim();
        String description = binding.etMealDescription.getText().toString().trim();
        String type = getSelectedType();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageAndSave(name, description, type);
        } else if (mealToEdit != null) {
            saveToFirestore(mealToEdit.getId(), name, description, type, mealToEdit.getImageUrl());
        } else {
            saveToFirestore(UUID.randomUUID().toString(), name, description, type, "");
        }
    }

    private String getSelectedType() {
        if (binding.rbBreakfast.isChecked()) return "Petit Déj";
        if (binding.rbDinner.isChecked()) return "Dîner";
        return "Déjeuner";
    }

    private void uploadImageAndSave(String name, String description, String type) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference ref = storage.getReference().child("meals/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String mealId = (mealToEdit != null) ? mealToEdit.getId() : UUID.randomUUID().toString();
                    saveToFirestore(mealId, name, description, type, uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur upload : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveToFirestore(String mealId, String name, String description, String type, String imageUrl) {
        Meal meal = new Meal(mealId, name, type, description, imageUrl, "", dayId);
        
        db.collection("restaurants").document(restaurantId)
                .collection("weeklyMenu").document(dayId)
                .collection("meals").document(mealId)
                .set(meal)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Plat enregistré !", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

package com.example.ugb_menu.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ugb_menu.databinding.ActivityAdminLoginBinding;
import com.example.ugb_menu.models.Admin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminLoginActivity extends AppCompatActivity {

    private ActivityAdminLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.btnLogin.setOnClickListener(v -> loginAdmin());
    }

    private void loginAdmin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkAdminRole(task.getResult().getUser().getUid());
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnLogin.setEnabled(true);
                        Toast.makeText(this, "Erreur d'authentification : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkAdminRole(String uid) {
        db.collection("admins").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);

                    if (documentSnapshot.exists()) {
                        Admin admin = documentSnapshot.toObject(Admin.class);
                        if (admin != null) {
                            goToDashboard(admin);
                        }
                    } else {
                        mAuth.signOut();
                        Toast.makeText(this, "Accès refusé : Vous n'êtes pas administrateur", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void goToDashboard(Admin admin) {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.putExtra("ADMIN_ROLE", admin.getRole());
        intent.putExtra("RESTO_ID", admin.getRestaurantId());
        startActivity(intent);
        finish();
    }
}

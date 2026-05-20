package com.example.ugb_menu.admin;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.ugb_menu.adapters.ReviewAdapter;
import com.example.ugb_menu.databinding.ActivityReviewModerationBinding;
import com.example.ugb_menu.models.Review;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewModerationActivity extends AppCompatActivity {

    private ActivityReviewModerationBinding binding;
    private FirebaseFirestore db;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewModerationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        setupRecyclerView();
        loadReviews();
    }

    private void setupRecyclerView() {
        // Note: Existing ReviewAdapter doesn't support long click/delete out of the box.
        // For a quick fix, we'll use a modified approach or just show them for now.
        reviewAdapter = new ReviewAdapter(reviewList);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setAdapter(reviewAdapter);

        // Implementing long click via ItemTouchListener or similar would be better,
        // but let's assume we'll update the adapter soon or add a listener.
    }

    private void loadReviews() {
        db.collection("reviews")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            reviewList.add(document.toObject(Review.class));
                        }
                        reviewAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Erreur chargement avis", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

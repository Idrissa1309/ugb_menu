package com.example.ugb_menu.repository;

import com.example.ugb_menu.models.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {
    private final DatabaseReference databaseReference;

    public ReviewRepository() {
        this.databaseReference = FirebaseDatabase.getInstance("https://ugb-menu-default-rtdb.firebaseio.com/").getReference("reviews");
    }

    public void addReview(Review review) {
        String id = databaseReference.push().getKey();
        if (id != null) {
            databaseReference.child(review.getMealId()).child(id).setValue(review);
        }
    }

    public void getReviewsForMeal(String mealId, final ReviewsCallback callback) {
        databaseReference.child(mealId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Review> reviews = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Review review = postSnapshot.getValue(Review.class);
                    if (review != null) {
                        reviews.add(review);
                    }
                }
                callback.onResult(reviews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public void getAverageRatingForMeal(String mealId, final AverageRatingCallback callback) {
        databaseReference.child(mealId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalRating = 0;
                int count = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Review review = postSnapshot.getValue(Review.class);
                    if (review != null) {
                        totalRating += review.getRating();
                        count++;
                    }
                }
                float average = count > 0 ? totalRating / count : 0;
                callback.onResult(average, count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(0, 0);
            }
        });
    }

    public interface ReviewsCallback {
        void onResult(List<Review> reviews);
    }

    public interface AverageRatingCallback {
        void onResult(float averageRating, int count);
    }
}

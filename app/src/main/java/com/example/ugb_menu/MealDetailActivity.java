package com.example.ugb_menu;

import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.ugb_menu.databinding.ActivityMealDetailBinding;
import com.example.ugb_menu.models.DayMenu;
import com.example.ugb_menu.models.Meal;
import com.example.ugb_menu.models.Restaurant;
import com.example.ugb_menu.repository.MenuRepository;
import com.example.ugb_menu.repository.ReviewRepository;
import com.example.ugb_menu.models.Review;
import com.example.ugb_menu.adapters.ReviewAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.ugb_menu.utils.FavoritesManager;
import com.example.ugb_menu.utils.ThemeManager;
import java.util.List;

public class MealDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MEAL = "extra_meal";
    private ActivityMealDetailBinding binding;
    private FavoritesManager favoritesManager;
    private MenuRepository menuRepository;
    private ReviewRepository reviewRepository;
    private Meal currentMeal;
    private List<Meal> todaysMeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ThemeManager(this).applyTheme(this);
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMealDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        favoritesManager = new FavoritesManager(this);
        menuRepository = new MenuRepository(this);
        reviewRepository = new ReviewRepository();
        currentMeal = (Meal) getIntent().getSerializableExtra(EXTRA_MEAL);
        todaysMeals = (List<Meal>) getIntent().getSerializableExtra("extra_todays_meals");

        if (currentMeal != null) {
            findTodaysMeals();
            setupUI();
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnFavorite.setOnClickListener(v -> toggleFavorite());
        binding.btnShare.setOnClickListener(v -> shareMeal());
        binding.btnSubmitReview.setOnClickListener(v -> submitReview());
        loadReviews();
        
        binding.tvTabLunch.setOnClickListener(v -> switchMealType("Déjeuner"));
        binding.tvTabDinner.setOnClickListener(v -> switchMealType("Dîner"));
    }

    private void findTodaysMeals() {
        if (todaysMeals != null) return; // Already loaded from intent possibly

        List<Restaurant> restaurants = menuRepository.getRestaurants();
        for (Restaurant r : restaurants) {
            if (r.getName() != null && r.getName().equals(currentMeal.getRestaurantName())) {
                for (DayMenu dm : r.getWeeklyMenu()) {
                    if (dm.getDate() != null && dm.getDate().equals(currentMeal.getDate())) {
                        todaysMeals = dm.getMeals();
                        return;
                    }
                }
            }
        }
    }

    private void switchMealType(String type) {
        if (todaysMeals == null) return;
        for (Meal m : todaysMeals) {
            if (m.getType().equals(type)) {
                currentMeal = m;
                setupUI();
                loadReviews();
                break;
            }
        }
    }

    private void setupUI() {
        binding.tvToolbarTitle.setText(currentMeal.getRestaurantName());
        binding.tvMealName.setText(currentMeal.getName());
        binding.tvDescription.setText(currentMeal.getDescription());
        binding.tvChipCategory.setText(currentMeal.getType());
        binding.tvInfoRestaurant.setText("Restaurant : " + currentMeal.getRestaurantName());
        binding.tvInfoType.setText("Type de repas : " + currentMeal.getType());
        binding.tvInfoDate.setText("Date : " + currentMeal.getDate());

        updateFavoriteIcon();

        Glide.with(this)
                .load(currentMeal.getImageUrl())
                .placeholder(R.drawable.logo_ugb)
                .centerCrop()
                .into(binding.ivMealImage);

        updateTabStyles();
    }

    private void updateTabStyles() {
        if ("Déjeuner".equals(currentMeal.getType())) {
            binding.tvTabLunch.setBackgroundResource(R.drawable.bg_segmented_selected);
            binding.tvTabLunch.setTextColor(Color.WHITE);
            binding.tvTabLunch.setTypeface(null, android.graphics.Typeface.BOLD);
            
            binding.tvTabDinner.setBackground(null);
            binding.tvTabDinner.setTextColor(Color.parseColor("#757575"));
            binding.tvTabDinner.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            binding.tvTabDinner.setBackgroundResource(R.drawable.bg_segmented_selected);
            binding.tvTabDinner.setTextColor(Color.WHITE);
            binding.tvTabDinner.setTypeface(null, android.graphics.Typeface.BOLD);
            
            binding.tvTabLunch.setBackground(null);
            binding.tvTabLunch.setTextColor(Color.parseColor("#757575"));
            binding.tvTabLunch.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    private void toggleFavorite() {
        if (favoritesManager.isFavorite(currentMeal.getId())) {
            favoritesManager.removeFavorite(currentMeal.getId());
        } else {
            favoritesManager.addFavorite(currentMeal.getId());
        }
        updateFavoriteIcon();
    }

    private void loadReviews() {
        if (currentMeal == null) return;
        
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        
        reviewRepository.getReviewsForMeal(currentMeal.getId(), reviews -> {
            ReviewAdapter adapter = new ReviewAdapter(reviews);
            binding.rvReviews.setAdapter(adapter);
            
            // Optionally update empty state if needed
            if (reviews.isEmpty()) {
                // You could show a placeholder "Soyez le premier à donner votre avis"
            }
        });
    }

    private void submitReview() {
        if (currentMeal == null) return;

        float rating = binding.ratingBar.getRating();
        String comment = binding.etComment.getText().toString().trim();

        if (rating == 0) {
            android.widget.Toast.makeText(this, "Veuillez donner une note", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        Review review = new Review(
                null, // ID will be generated by repository
                currentMeal.getId(),
                rating,
                comment,
                System.currentTimeMillis()
        );

        reviewRepository.addReview(review);
        
        // Reset UI
        binding.ratingBar.setRating(0);
        binding.etComment.setText("");
        
        // Hide keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        android.widget.Toast.makeText(this, "Merci pour votre avis !", android.widget.Toast.LENGTH_SHORT).show();
    }

    private void shareMeal() {
        if (currentMeal == null || todaysMeals == null) return;

        StringBuilder shareText = new StringBuilder();
        shareText.append("🍴 *MENU DU JOUR - ").append(currentMeal.getRestaurantName()).append("*\n");
        shareText.append("📅 ").append(currentMeal.getDate()).append("\n\n");

        for (Meal meal : todaysMeals) {
            shareText.append(meal.getType().equals("Déjeuner") ? "☀️ " : "🌙 ")
                    .append("*").append(meal.getType()).append(" :*\n")
                    .append("👉 ").append(meal.getName()).append("\n")
                    .append("_").append(meal.getDescription()).append("_\n\n");
        }

        shareText.append("Bon appétit ! 😊\n_Partagé via UGB Menu_");

        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareText.toString());
        startActivity(android.content.Intent.createChooser(intent, "Partager le menu complet via"));
    }

    private void updateFavoriteIcon() {
        boolean isFav = favoritesManager.isFavorite(currentMeal.getId());
        binding.btnFavorite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }
}

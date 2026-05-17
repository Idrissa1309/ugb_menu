package com.example.ugb_menu.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.ugb_menu.models.Meal;
import com.example.ugb_menu.models.Restaurant;
import com.example.ugb_menu.repository.MenuRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MealAlertWorker extends Worker {

    public MealAlertWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        MenuRepository repository = new MenuRepository(context);
        FavoritesManager favoritesManager = new FavoritesManager(context);
        Set<String> favoriteIds = favoritesManager.getFavoriteIds();

        if (favoriteIds.isEmpty()) return Result.success();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        List<Restaurant> restaurants = repository.getRestaurants();
        for (Restaurant restaurant : restaurants) {
            for (com.example.ugb_menu.models.DayMenu day : restaurant.getWeeklyMenu()) {
                if (day.getDate().equals(today)) {
                    for (Meal meal : day.getMeals()) {
                        if (favoriteIds.contains(meal.getId())) {
                            NotificationHelper.showMealNotification(
                                    context,
                                    "Plat Favori Aujourd'hui !",
                                    "Le plat '" + meal.getName() + "' est servi au " + restaurant.getName()
                            );
                        }
                    }
                }
            }
        }

        return Result.success();
    }
}

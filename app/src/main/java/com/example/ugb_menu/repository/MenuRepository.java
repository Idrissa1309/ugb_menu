package com.example.ugb_menu.repository;

import android.content.Context;
import com.example.ugb_menu.models.Meal;
import com.example.ugb_menu.models.MealEntity;
import com.example.ugb_menu.models.MenuResponse;
import com.example.ugb_menu.models.Restaurant;
import com.example.ugb_menu.repository.local.AppDatabase;
import com.example.ugb_menu.repository.local.MealDao;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.io.FileOutputStream;
import android.util.Log;

public class MenuRepository {
    private final Context context;
    private final Gson gson;
    private final MealDao mealDao;
    private final ExecutorService executorService;
    private final FirebaseFirestore db;
    private static final String MENU_CACHE_FILE = "menu_cache.json";

    public MenuRepository(Context context) {
        this.context = context;
        this.gson = new Gson();
        this.mealDao = AppDatabase.getInstance(context).mealDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.db = FirebaseFirestore.getInstance();
    }

    public void getMenuAsync(Callback<List<Restaurant>> callback) {
        Log.d("MenuRepository", "Fetching restaurants from Firestore...");
        db.collection("restaurants").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Restaurant> restaurants = new ArrayList<>();
                Log.d("MenuRepository", "Firestore successful, documents count: " + task.getResult().size());
                
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("MenuRepository", "Document ID: " + document.getId() + " Data: " + document.getData());
                    try {
                        Restaurant restaurant = document.toObject(Restaurant.class);
                        if (restaurant != null) {
                            restaurants.add(restaurant);
                            Log.d("MenuRepository", "Parsed restaurant: " + restaurant.getName());
                            if (restaurant.getWeeklyMenu() != null) {
                                Log.d("MenuRepository", "Weekly menu size: " + restaurant.getWeeklyMenu().size());
                            } else {
                                Log.w("MenuRepository", "Weekly menu is NULL for " + restaurant.getName());
                            }
                        }
                    } catch (Exception e) {
                        Log.e("MenuRepository", "Error parsing restaurant document: " + document.getId(), e);
                    }
                }
                
                if (!restaurants.isEmpty()) {
                    saveToCache(gson.toJson(new MenuResponse(restaurants)));
                    callback.onResult(restaurants);
                } else {
                    Log.w("MenuRepository", "Restaurants list is empty after parsing");
                    fallbackToCache(callback);
                }
            } else {
                Log.e("MenuRepository", "Firestore fetch failed or result is null", task.getException());
                fallbackToCache(callback);
            }
        });
    }

    private void fallbackToCache(Callback<List<Restaurant>> callback) {
        executorService.execute(() -> {
            String cachedJson = loadFromCache();
            if (cachedJson != null) {
                callback.onResult(parseJson(cachedJson));
            } else {
                String assetJson = loadJSONFromAsset("menu.json");
                callback.onResult(parseJson(assetJson));
            }
        });
    }

    private List<Restaurant> parseJson(String json) {
        if (json == null) return new ArrayList<>();
        try {
            MenuResponse response = gson.fromJson(json, MenuResponse.class);
            return response.getRestaurants();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveToCache(String json) {
        try (FileOutputStream fos = context.openFileOutput(MENU_CACHE_FILE, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loadFromCache() {
        File file = new File(context.getFilesDir(), MENU_CACHE_FILE);
        if (!file.exists()) return null;
        try {
            return readFile(file);
        } catch (IOException e) {
            return null;
        }
    }

    private String readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            FileChannel fc = fis.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return StandardCharsets.UTF_8.decode(bb).toString();
        }
    }

    /**
     * Gets meals for a specific date from the local database.
     */
    public void getMealsForDate(String date, Callback<List<Meal>> callback) {
        executorService.execute(() -> {
            List<MealEntity> entities = mealDao.getMealsForDate(date);
            List<Meal> meals = new ArrayList<>();
            for (MealEntity entity : entities) {
                meals.add(entity.toMeal());
            }
            callback.onResult(meals);
        });
    }

    /**
     * Finds the index of the day in the weekly menu corresponding to a given date.
     * @param dateStr Date in "yyyy-MM-dd" format
     * @return The index or -1 if not found.
     */
    public int getDayIndexForDate(String dateStr, List<Restaurant> restaurants) {
        if (restaurants == null || restaurants.isEmpty()) {
            Log.w("MenuRepository", "getDayIndexForDate: restaurants list is empty");
            return -1;
        }

        Log.d("MenuRepository", "Searching index for date: " + dateStr);

        for (Restaurant restaurant : restaurants) {
            List<com.example.ugb_menu.models.DayMenu> weeklyMenu = restaurant.getWeeklyMenu();
            if (weeklyMenu == null) continue;

            for (int i = 0; i < weeklyMenu.size(); i++) {
                if (dateStr.equals(weeklyMenu.get(i).getDate())) {
                    Log.d("MenuRepository", "Found index " + i + " for date " + dateStr + " in restaurant " + restaurant.getName());
                    return i;
                }
            }
        }
        
        Log.w("MenuRepository", "No index found for date: " + dateStr);
        return -1;
    }

    public List<Restaurant> getRestaurants() {
        return getRestaurantsSync();
    }

    public List<Restaurant> getRestaurantsSync() {
        String cachedJson = loadFromCache();
        if (cachedJson != null) return parseJson(cachedJson);
        
        String assetJson = loadJSONFromAsset("menu.json");
        return parseJson(assetJson);
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    private String loadJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}

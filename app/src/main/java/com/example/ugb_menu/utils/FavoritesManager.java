package com.example.ugb_menu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class FavoritesManager {
    private static final String PREF_NAME = "ugb_menu_favorites";
    private static final String KEY_FAVORITES = "favorite_meals";
    private final SharedPreferences sharedPreferences;

    public FavoritesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addFavorite(String mealId) {
        Set<String> favorites = getFavoriteIds();
        favorites.add(mealId);
        sharedPreferences.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }

    public void removeFavorite(String mealId) {
        Set<String> favorites = getFavoriteIds();
        favorites.remove(mealId);
        sharedPreferences.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }

    public boolean isFavorite(String mealId) {
        return getFavoriteIds().contains(mealId);
    }

    public Set<String> getFavoriteIds() {
        return new HashSet<>(sharedPreferences.getStringSet(KEY_FAVORITES, new HashSet<>()));
    }
}

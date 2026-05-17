package com.example.ugb_menu.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meals")
public class MealEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String type;
    private String description;
    private String imageUrl;
    private String restaurantName;
    private String date;

    public MealEntity(@NonNull String id, String name, String type, String description, String imageUrl, String restaurantName, String date) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.imageUrl = imageUrl;
        this.restaurantName = restaurantName;
        this.date = date;
    }

    @NonNull
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getRestaurantName() { return restaurantName; }
    public String getDate() { return date; }

    public static MealEntity fromMeal(Meal meal) {
        return new MealEntity(
                meal.getId(),
                meal.getName(),
                meal.getType(),
                meal.getDescription(),
                meal.getImageUrl(),
                meal.getRestaurantName(),
                meal.getDate()
        );
    }

    public Meal toMeal() {
        return new Meal(id, name, type, description, imageUrl, restaurantName, date);
    }
}

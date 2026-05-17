package com.example.ugb_menu.models;

import java.io.Serializable;

public class Meal implements Serializable {
    private String id;
    private String name;
    private String type; // Déjeuner or Dîner
    private String description;
    private String imageUrl;
    private String restaurantName;
    private String date;

    public Meal() {}

    public Meal(String id, String name, String type, String description, String imageUrl, String restaurantName, String date) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.imageUrl = imageUrl;
        this.restaurantName = restaurantName;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}

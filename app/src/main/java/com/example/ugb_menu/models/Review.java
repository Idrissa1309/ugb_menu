package com.example.ugb_menu.models;

public class Review {
    private String id;
    private String mealId;
    private float rating;
    private String comment;
    private long timestamp;

    public Review() {
        // Required for Firebase
    }

    public Review(String id, String mealId, float rating, String comment, long timestamp) {
        this.id = id;
        this.mealId = mealId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getMealId() { return mealId; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public long getTimestamp() { return timestamp; }
}

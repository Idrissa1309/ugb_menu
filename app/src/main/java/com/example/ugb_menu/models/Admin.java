package com.example.ugb_menu.models;

public class Admin {
    private String uid;
    private String role; // "super_admin" | "manager"
    private String restaurantId; // null for super_admin
    private String name;
    private String email;

    public Admin() {
        // Required for Firestore
    }

    public Admin(String uid, String role, String restaurantId, String name, String email) {
        this.uid = uid;
        this.role = role;
        this.restaurantId = restaurantId;
        this.name = name;
        this.email = email;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

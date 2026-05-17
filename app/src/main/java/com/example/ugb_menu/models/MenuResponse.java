package com.example.ugb_menu.models;

import java.util.List;

public class MenuResponse {
    private List<Restaurant> restaurants;

    public MenuResponse(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }
}

package com.example.ugb_menu.models;

import java.util.List;

public class DayMenu {
    private String date;
    private List<Meal> meals;

    public DayMenu() {}

    public DayMenu(String date, List<Meal> meals) {
        this.date = date;
        this.meals = meals;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<Meal> getMeals() { return meals; }
    public void setMeals(List<Meal> meals) { this.meals = meals; }
}

package com.example.ugb_menu.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Restaurant {
    private String id;
    private String name;
    private List<DayMenu> weeklyMenu;

    public Restaurant() {}

    public Restaurant(String id, String name, List<DayMenu> weeklyMenu) {
        this.id = id;
        this.name = name;
        this.weeklyMenu = weeklyMenu;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<DayMenu> getWeeklyMenu() { return weeklyMenu; }
    public void setWeeklyMenu(List<DayMenu> weeklyMenu) { this.weeklyMenu = weeklyMenu; }
}

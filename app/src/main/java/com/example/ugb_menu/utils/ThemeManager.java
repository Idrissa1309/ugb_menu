package com.example.ugb_menu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.ugb_menu.R;

public class ThemeManager {
    private static final String PREF_NAME = "ugb_menu_theme_prefs";
    private static final String KEY_THEME = "selected_theme";
    
    public static final String THEME_GREEN = "green";
    public static final String THEME_BLUE = "blue";
    public static final String THEME_BLACK = "black";
    public static final String THEME_MAROON = "maroon";

    private final SharedPreferences prefs;

    public ThemeManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setTheme(String themeName) {
        prefs.edit().putString(KEY_THEME, themeName).apply();
    }

    public String getTheme() {
        return prefs.getString(KEY_THEME, THEME_GREEN);
    }

    public void applyTheme(android.app.Activity activity) {
        String theme = getTheme();
        switch (theme) {
            case THEME_BLUE:
                activity.setTheme(R.style.Theme_UGBMenu_Blue);
                break;
            case THEME_BLACK:
                activity.setTheme(R.style.Theme_UGBMenu_Black);
                break;
            case THEME_MAROON:
                activity.setTheme(R.style.Theme_UGBMenu_Maroon);
                break;
            default:
                activity.setTheme(R.style.Theme_UGBMenu);
                break;
        }
    }
}

package com.example.ugb_menu.utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.example.ugb_menu.MainActivity;
import com.example.ugb_menu.R;
import com.example.ugb_menu.models.Meal;
import com.example.ugb_menu.models.Restaurant;
import com.example.ugb_menu.repository.MenuRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MenuWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_meal);

        // Click on widget title opens the app
        Intent intent = new Intent(context, MainActivity.class);
        // Using FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE is mandatory for Android 12+
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);
        views.setOnClickPendingIntent(R.id.widget_meal_name, pendingIntent);

        // Fetch today's menu (simpler version for widget)
        MenuRepository repository = new MenuRepository(context);
        List<Restaurant> restaurants = repository.getRestaurants();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        boolean found = false;
        if (!restaurants.isEmpty()) {
            for (Restaurant restaurant : restaurants) {
                for (com.example.ugb_menu.models.DayMenu day : restaurant.getWeeklyMenu()) {
                    if (day.getDate().equals(today) && !day.getMeals().isEmpty()) {
                        Meal firstMeal = day.getMeals().get(0);
                        views.setTextViewText(R.id.widget_meal_name, firstMeal.getName());
                        views.setTextViewText(R.id.widget_resto_name, restaurant.getName());
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
        }

        if (!found) {
            views.setTextViewText(R.id.widget_meal_name, "Aucun menu");
            views.setTextViewText(R.id.widget_resto_name, "pour aujourd'hui");
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

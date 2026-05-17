package com.example.ugb_menu.repository.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.ugb_menu.models.MealEntity;
import java.util.List;

@Dao
public interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeals(List<MealEntity> meals);

    @Query("SELECT * FROM meals WHERE date = :date")
    List<MealEntity> getMealsForDate(String date);

    @Query("SELECT * FROM meals")
    List<MealEntity> getAllMeals();

    @Query("DELETE FROM meals")
    void deleteAll();
}

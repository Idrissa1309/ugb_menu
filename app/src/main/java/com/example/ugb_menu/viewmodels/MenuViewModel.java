package com.example.ugb_menu.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ugb_menu.models.Restaurant;
import com.example.ugb_menu.repository.MenuRepository;
import java.util.List;

public class MenuViewModel extends AndroidViewModel {
    private final MenuRepository repository;
    private final MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    public MenuViewModel(@NonNull Application application) {
        super(application);
        this.repository = new MenuRepository(application);
        loadMenu();
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        return restaurants;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadMenu() {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        repository.getMenuAsync(new MenuRepository.Callback<List<Restaurant>>() {
            @Override
            public void onResult(List<Restaurant> result) {
                isLoading.postValue(false);
                if (result != null && !result.isEmpty()) {
                    restaurants.postValue(result);
                } else {
                    errorMessage.postValue("Impossible de charger les menus. Vérifiez votre connexion.");
                }
            }
        });
    }
}

package com.example.ugb_menu;

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class UGBApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Configuration de Cloudinary
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", getResources().getString(R.string.CLOUD_NAME)); // À remplacer par votre Cloud Name
        config.put("secure", "true");
        MediaManager.init(this, config);
    }
}

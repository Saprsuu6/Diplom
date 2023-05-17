package com.example.instagram.services;

import android.app.Activity;
import android.view.View;
import android.view.Window;

public class UiVisibility {
    public static void setUiVisibility(Activity activity) {
        Window w = activity.getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}

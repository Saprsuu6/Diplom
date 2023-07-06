package com.example.instagram.main_process;

import android.app.Activity;
import android.widget.ImageView;

import com.example.instagram.services.FindUser;
import com.example.instagram.services.Intents;

import org.json.JSONException;

public class BottomMenu {
    public static void setListeners(Activity activity, ImageView[] imageViews, FindUser findUser) {
        // find users
        imageViews[0].setOnClickListener(v -> {
            try {
                findUser.getToFindUser(null).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        // new post
        imageViews[1].setOnClickListener(v -> activity.startActivity(Intents.getCreateNewPost()));

        // notifications
        imageViews[2].setOnClickListener(v -> activity.startActivity(Intents.getNotifications()));
    }
}

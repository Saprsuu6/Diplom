package com.example.instagram.main_process;

import android.app.Activity;
import android.widget.ImageView;

import androidx.core.widget.NestedScrollView;

import com.example.instagram.R;
import com.example.instagram.services.FindUser;
import com.example.instagram.services.Intents;

public class BottomMenu {
    public static void setListeners(Activity activity, ImageView[] imageViews, FindUser findUser) {
        // find users
        imageViews[0].setOnClickListener(v -> {
            findUser.getToFindUser().show();
        });

        // new post
        imageViews[1].setOnClickListener(v -> {
            activity.startActivity(Intents.getCreateNewPost());
        });

        // TODO: notifications [2]
    }
}

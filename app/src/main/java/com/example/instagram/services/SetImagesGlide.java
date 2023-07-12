package com.example.instagram.services;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class SetImagesGlide {
    public static void setImageGlide(Activity activity, String path, ImageView destination) {
        Glide.with(activity.getApplicationContext()).load(path).diskCacheStrategy(DiskCacheStrategy.ALL).into(destination);
    }
}

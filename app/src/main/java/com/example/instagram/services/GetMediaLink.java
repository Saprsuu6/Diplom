package com.example.instagram.services;

import android.app.Activity;

import com.example.instagram.R;

public class GetMediaLink {
    public static String getMediaLink(Activity activity, String link) {
        return activity.getResources().getString(R.string.root_media_folder) + link;
    }
}

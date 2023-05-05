package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.instagram.R;
import com.example.instagram.services.themes_and_backgrounds.Backgrounds;

public class PostInDialog {
    public static AlertDialog.Builder getPostDialog(Context context, Resources resources) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context)
                .inflate(R.layout.post, null, false);

        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(view)
                .setPositiveButton(resources.getString(R.string.permission_ok), null);
    }
}

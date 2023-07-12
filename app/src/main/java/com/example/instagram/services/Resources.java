package com.example.instagram.services;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.R;

public class Resources {
    public static Toast getToast(Activity activity, String message) {
        return Toast.makeText(activity, message, Toast.LENGTH_SHORT);
    }

    public static void setText(String message, View viewTo) {
        if (viewTo instanceof EditText) ((EditText) viewTo).setText(message);
        if (viewTo instanceof TextView) ((TextView) viewTo).setText(message);
    }

    public static void setHintIntoEditText(String hint, EditText viewTo) {
        viewTo.setHint(hint);
    }

    public static void setDrawableIntoImageView(Drawable drawable, ImageView viewTo) {
        viewTo.setImageDrawable(drawable);
    }

    public static void setTextColor(int color, View viewTo) {
        if (viewTo instanceof EditText) ((EditText) viewTo).setTextColor(color);
        if (viewTo instanceof TextView) ((TextView) viewTo).setTextColor(color);
    }

    public static void setBackgroundForEditText(Drawable drawable, EditText viewTo) {
        viewTo.setBackground(drawable);
    }

    public static void setVisibility(int visibility, View viewTo) {
        viewTo.setVisibility(visibility);
    }

    public static void startAnimation(Animation animation, View viewTo) {
        viewTo.startAnimation(animation);
    }

    public static void setEllipsize(TextUtils.TruncateAt ellipsize, TextView viewTo) {
        viewTo.setEllipsize(ellipsize);
    }
}

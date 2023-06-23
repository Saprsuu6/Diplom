package com.example.instagram.services;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;

public class Animation {
    public static AnimationDrawable getAnimations(View view) {
        AnimationDrawable animationDrawable = (AnimationDrawable) view.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        return animationDrawable;
    }
}

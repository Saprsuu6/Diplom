package com.example.instagram.services.themes_and_backgrounds;

import androidx.appcompat.app.AppCompatDelegate;

public enum Themes {
    SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    DAY(AppCompatDelegate.MODE_NIGHT_NO),
    NIGHT(AppCompatDelegate.MODE_NIGHT_YES);

    private final int value;

    Themes(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}

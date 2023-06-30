package com.example.instagram.services.themes_and_backgrounds;

import com.example.instagram.R;

public enum Backgrounds {
    Background0(R.drawable.main_theme), Background1(R.drawable.theme_1), Background2(R.drawable.theme_2), Background3(R.drawable.theme_3), Background4(R.drawable.theme_4), Background5(R.drawable.theme_5);

    private final int value;

    Backgrounds(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}

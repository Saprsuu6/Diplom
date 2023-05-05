package com.example.instagram.services.themes_and_backgrounds;

import com.example.instagram.R;

public enum Backgrounds {
    Background0(R.drawable.gradient_list_main),
    Background1(R.drawable.gradient_1),
    Background2(R.drawable.gradient_2),
    Background3(R.drawable.gradient_3),
    Background4(R.drawable.gradient_4),
    Background5(R.drawable.gradient_5),
    Background6(R.drawable.gradient_6);

    private final int value;

    Backgrounds(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}

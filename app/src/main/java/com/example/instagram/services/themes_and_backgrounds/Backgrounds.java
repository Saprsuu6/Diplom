package com.example.instagram.services.themes_and_backgrounds;

import com.example.instagram.R;

public enum Backgrounds {
    Background0(R.drawable.main_theme), Background0Top(R.drawable.main_theme_top), Background0Other(R.drawable.main_theme_other), Background1(R.drawable.theme_1), Background1Top(R.drawable.theme_1_top), Background1Other(R.drawable.theme_1_other), Background2(R.drawable.theme_2), Background2Top(R.drawable.theme_2_top), Background2Other(R.drawable.theme_2_other), Background3(R.drawable.theme_3), Background3Top(R.drawable.theme_3_top), Background3Other(R.drawable.theme_3_other), Background4(R.drawable.theme_4), Background4Top(R.drawable.theme_4_top), Background4Other(R.drawable.theme_4_other), Background5(R.drawable.theme_5), Background5Top(R.drawable.theme_5_top), Background5Other(R.drawable.theme_5_other);

    private final int value;

    Backgrounds(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }

    public static int getTop(int theme) {
        if (theme == Backgrounds.Background0.getValue()) {
            return Background0Top.value;
        } else if (theme == Backgrounds.Background1.getValue()) {
            return Background1Top.value;
        } else if (theme == Backgrounds.Background2.getValue()) {
            return Background2Top.value;
        } else if (theme == Backgrounds.Background3.getValue()) {
            return Background3Top.value;
        } else if (theme == Backgrounds.Background4.getValue()) {
            return Background4Top.value;
        } else if (theme == Backgrounds.Background5.getValue()) {
            return Background5Top.value;
        } else {
            return Background0Top.value;
        }
    }

    public static int getOther(int theme) {
        if (theme == Backgrounds.Background0.getValue()) {
            return Background0Other.value;
        } else if (theme == Backgrounds.Background1.getValue()) {
            return Background1Other.value;
        } else if (theme == Backgrounds.Background2.getValue()) {
            return Background2Other.value;
        } else if (theme == Backgrounds.Background3.getValue()) {
            return Background3Other.value;
        } else if (theme == Backgrounds.Background4.getValue()) {
            return Background4Other.value;
        } else if (theme == Backgrounds.Background5.getValue()) {
            return Background5Other.value;
        } else {
            return Background0Other.value;
        }
    }
}

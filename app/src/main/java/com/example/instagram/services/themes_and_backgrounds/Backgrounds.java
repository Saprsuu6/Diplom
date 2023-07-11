package com.example.instagram.services.themes_and_backgrounds;

import com.example.instagram.R;

public enum Backgrounds {
    Background0(R.drawable.main_theme), Background0Top(R.drawable.main_theme_top), Background0Other(R.drawable.main_theme_bottom), Background1(R.drawable.theme_1), Background1Top(R.drawable.theme_1_top), Background1Other(R.drawable.theme_1_bottm), Background2(R.drawable.theme_2), Background2Top(R.drawable.theme_2_top), Background2Other(R.drawable.theme_2_bottom), Background3(R.drawable.theme_3), Background3Top(R.drawable.theme_3_top), Background3Other(R.drawable.theme_3_bottom), Background4(R.drawable.theme_4), Background4Top(R.drawable.theme_4_top), Background4Other(R.drawable.theme_4_bottm), Background5(R.drawable.theme_5), Background5Top(R.drawable.theme_5_top), Background5Other(R.drawable.theme_5_bottm), Background0Template(R.drawable.main_theme_template), Background0Template2(R.drawable.main_theme_template_2), Background1Template(R.drawable.theme_1_template), Background2Template(R.drawable.theme_2_template), Background3Template(R.drawable.theme_3_template), Background4Template(R.drawable.theme_4_template), Background5Template(R.drawable.theme_5_template);

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

    public static int getTemplate(int theme) {
        if (theme == Backgrounds.Background0.getValue()) {
            return Background0Template.value;
        } else if (theme == Backgrounds.Background1.getValue()) {
            return Background1Template.value;
        } else if (theme == Backgrounds.Background2.getValue()) {
            return Background2Template.value;
        } else if (theme == Backgrounds.Background3.getValue()) {
            return Background3Template.value;
        } else if (theme == Backgrounds.Background4.getValue()) {
            return Background4Template.value;
        } else if (theme == Backgrounds.Background5.getValue()) {
            return Background5Template.value;
        } else {
            return Background0Template.value;
        }
    }
}

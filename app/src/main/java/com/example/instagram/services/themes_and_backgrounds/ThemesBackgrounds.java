package com.example.instagram.services.themes_and_backgrounds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.example.instagram.R;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.GetDialog;

public class ThemesBackgrounds {

    public static Themes theme = Themes.SYSTEM;
    public static int background = Backgrounds.Background0.getValue();

    public static boolean isNight(Resources resources) {
        int currentNightMode = resources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static Dialog getThemeDialog(Activity activity, LinearLayout setBackground) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(activity).inflate(R.layout.choose_theme, null, true);
        setListeners(activity, view, setBackground);

        Button close = view.findViewById(R.id.close);

        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);

        Dialog dialog = GetDialog.getDialog(activity, view);

        close.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static void setListeners(Activity activity, View view, LinearLayout setBackground) {
        RadioButton mainTheme = view.findViewById(R.id.main_theme);
        RadioButton first = view.findViewById(R.id.first_theme);
        RadioButton second = view.findViewById(R.id.second_theme);
        RadioButton third = view.findViewById(R.id.third_theme);
        RadioButton fourth = view.findViewById(R.id.fourth_theme);
        RadioButton fives = view.findViewById(R.id.fives_theme);

        mainTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background0.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background0.getValue(), setBackground);
            }
        });

        first.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background1.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background1.getValue(), setBackground);
            }
        });

        second.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background2.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background2.getValue(), setBackground);
            }
        });

        third.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background3.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background3.getValue(), setBackground);
            }
        });

        fourth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background4.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background4.getValue(), setBackground);
            }
        });

        fives.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background5.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background5.getValue(), setBackground);
            }
        });

        //region check prefer theme
        int background = Cache.loadIntSP(activity, CacheScopes.USER_PREFER_THEME.toString());

        if (background == Backgrounds.Background0.getValue()) {
            mainTheme.setChecked(true);
        } else if (background == Backgrounds.Background1.getValue()) {
            first.setChecked(true);
        } else if (background == Backgrounds.Background2.getValue()) {
            second.setChecked(true);
        } else if (background == Backgrounds.Background3.getValue()) {
            third.setChecked(true);
        } else if (background == Backgrounds.Background4.getValue()) {
            fourth.setChecked(true);
        } else if (background == Backgrounds.Background5.getValue()) {
            fives.setChecked(true);
        } else {
            mainTheme.setChecked(true);
        }
        //endregion
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static void saveBackgroundState(Activity activity, int value, View setBackground) {
        saveSP(activity, CacheScopes.LAST_THEME.toString(), value);
        Cache.saveSP(activity, CacheScopes.USER_PREFER_THEME.toString(), value);
        background = value;

        setBackground.setBackground(activity.getResources().getDrawable(value, activity.getTheme()));
    }

    public static void saveSP(Activity activity, String key, int value) {
        Cache.saveSP(activity, key, value);
    }

    public static int loadSP(Activity activity, String key) {
        return Cache.loadIntSP(activity, key);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void loadBackground(Activity activity, View linearLayout) { // load bg after resume activity
        int idBackground = loadSP(activity, CacheScopes.LAST_THEME.toString());
        linearLayout.setBackground(idBackground != 0 ? activity.getResources().getDrawable(idBackground, activity.getTheme()) : activity.getResources().getDrawable(R.drawable.main_theme, activity.getTheme()));
        saveBackgroundState(activity, idBackground == 0 ? Backgrounds.Background0.getValue() : idBackground, linearLayout);
        loadOtherBackgrounds(activity, idBackground, linearLayout);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static void loadOtherBackgrounds(Activity activity, int idBackground, View linearLayout) {
        try {
            View view = linearLayout.findViewById(R.id.top);
            view.setBackground(activity.getResources().getDrawable(Backgrounds.getTop(idBackground), activity.getTheme()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            View view = linearLayout.findViewById(R.id.bottom_menu);
            view.setBackground(activity.getResources().getDrawable(Backgrounds.getOther(idBackground), activity.getTheme()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setThemeContent(Resources resources, ImageView imageView, Context activity) {
        imageView.setImageDrawable(ThemesBackgrounds.isNight(resources)
                ? resources.getDrawable(R.drawable.sun, activity.getTheme())
                : resources.getDrawable(R.drawable.moon, activity.getTheme()));
    }
}

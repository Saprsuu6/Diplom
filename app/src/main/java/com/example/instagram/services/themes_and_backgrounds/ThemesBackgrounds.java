package com.example.instagram.services.themes_and_backgrounds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.instagram.R;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;

public class ThemesBackgrounds {

    public static Themes theme = Themes.SYSTEM;
    public static int background = Backgrounds.Background0.getValue();

    public static boolean isNight(Resources resources) {
        int currentNightMode = resources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static AlertDialog.Builder getThemeDialog(Context context, Resources resources, Activity activity, LinearLayout setBackground) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setPadding(0, 50, 0, 0);

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.setGravity(Gravity.CENTER);

        RadioButton[] radioButtons = getRadioButtons(context, resources, activity, setBackground);

        // region set to views
        for (RadioButton radioButton : radioButtons) {
            radioGroup.addView(radioButton);
        }
        linearLayout.addView(radioGroup);
        // endregion

        // region select
        int background = Cache.loadIntSP(context, CacheScopes.USER_PREFER_THEME.toString());

        if (background == Backgrounds.Background0.getValue()) {
            radioButtons[0].setChecked(true);
        } else if (background == Backgrounds.Background1.getValue()) {
            radioButtons[1].setChecked(true);
        } else if (background == Backgrounds.Background2.getValue()) {
            radioButtons[2].setChecked(true);
        } else if (background == Backgrounds.Background3.getValue()) {
            radioButtons[3].setChecked(true);
        } else if (background == Backgrounds.Background4.getValue()) {
            radioButtons[4].setChecked(true);
        } else if (background == Backgrounds.Background5.getValue()) {
            radioButtons[5].setChecked(true);
        } else {
            radioButtons[0].setChecked(true);
        }

        // endregion

        // TODO save prefer theme load user on start app

        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(linearLayout)
                .setPositiveButton(resources.getString(R.string.permission_ok), null);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static RadioButton[] getRadioButtons(Context context, Resources resources, Activity activity, LinearLayout setBackground) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(700, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);

        // region create check box
        RadioButton radioButton0 = new RadioButton(context);
        radioButton0.setLayoutParams(layoutParams);
        radioButton0.setBackground(resources.getDrawable(R.drawable.main_theme_template, context.getTheme()));

        RadioButton radioButton1 = new RadioButton(context);
        radioButton1.setLayoutParams(layoutParams);
        radioButton1.setBackground(resources.getDrawable(R.drawable.theme_1_template, context.getTheme()));

        RadioButton radioButton2 = new RadioButton(context);
        radioButton2.setLayoutParams(layoutParams);
        radioButton2.setBackground(resources.getDrawable(R.drawable.theme_2_template, context.getTheme()));

        RadioButton radioButton3 = new RadioButton(context);
        radioButton3.setLayoutParams(layoutParams);
        radioButton3.setBackground(resources.getDrawable(R.drawable.theme_3_template, context.getTheme()));

        RadioButton radioButton4 = new RadioButton(context);
        radioButton4.setLayoutParams(layoutParams);
        radioButton4.setBackground(resources.getDrawable(R.drawable.theme_4_template, context.getTheme()));

        RadioButton radioButton5 = new RadioButton(context);
        radioButton5.setLayoutParams(layoutParams);
        radioButton5.setBackground(resources.getDrawable(R.drawable.theme_5_template, context.getTheme()));
        // endregion

        // region set listeners
        radioButton0.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background0.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background0.getValue(), setBackground);
            }
        });

        radioButton1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background1.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background1.getValue(), setBackground);
            }
        });

        radioButton2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background2.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background2.getValue(), setBackground);
            }
        });

        radioButton3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background3.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background3.getValue(), setBackground);
            }
        });

        radioButton4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background4.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background4.getValue(), setBackground);
            }
        });

        radioButton5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background5.getValue(), setBackground);
                loadOtherBackgrounds(activity, Backgrounds.Background5.getValue(), setBackground);
            }
        });
        // endregion

        return new RadioButton[]{radioButton0, radioButton1, radioButton2, radioButton3, radioButton4, radioButton5};
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
    public static void setThemeContent(Resources resources, ImageView imageView, Context context) {
        imageView.setImageDrawable(ThemesBackgrounds.isNight(resources)
                ? resources.getDrawable(R.drawable.sun, context.getTheme())
                : resources.getDrawable(R.drawable.moon, context.getTheme()));
    }
}

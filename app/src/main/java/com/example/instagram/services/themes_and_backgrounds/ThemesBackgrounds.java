package com.example.instagram.services.themes_and_backgrounds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.instagram.R;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.TransitUser;

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
        int background = TransitUser.user.getOtherInfo().getPreferBackground();

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
        } else if (background == Backgrounds.Background6.getValue()) {
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
        radioButton0.setBackground(resources.getDrawable(R.drawable.gradient_list_main_template, context.getTheme()));

        AnimationDrawable animationDrawable = (AnimationDrawable) radioButton0.getBackground();
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();

        RadioButton radioButton1 = new RadioButton(context);
        radioButton1.setLayoutParams(layoutParams);
        radioButton1.setBackground(resources.getDrawable(R.drawable.gradient_1_template, context.getTheme()));

        RadioButton radioButton2 = new RadioButton(context);
        radioButton2.setLayoutParams(layoutParams);
        radioButton2.setBackground(resources.getDrawable(R.drawable.gradient_2_template, context.getTheme()));

        RadioButton radioButton3 = new RadioButton(context);
        radioButton3.setLayoutParams(layoutParams);
        radioButton3.setBackground(resources.getDrawable(R.drawable.gradient_3_template, context.getTheme()));

        RadioButton radioButton4 = new RadioButton(context);
        radioButton4.setLayoutParams(layoutParams);
        radioButton4.setBackground(resources.getDrawable(R.drawable.gradient_4_template, context.getTheme()));

        RadioButton radioButton5 = new RadioButton(context);
        radioButton5.setLayoutParams(layoutParams);
        radioButton5.setBackground(resources.getDrawable(R.drawable.gradient_5_template, context.getTheme()));

        RadioButton radioButton6 = new RadioButton(context);
        radioButton6.setLayoutParams(layoutParams);
        radioButton6.setBackground(resources.getDrawable(R.drawable.gradient_6_template, context.getTheme()));
        // endregion

        // region set listeners
        radioButton0.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background0.getValue(), setBackground);

                AnimationDrawable animationDrawableActivity
                        = (AnimationDrawable) setBackground.getBackground();
                animationDrawableActivity.setExitFadeDuration(4000);
                animationDrawableActivity.start();
            }
        });

        radioButton1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background1.getValue(), setBackground);
            }
        });

        radioButton2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background2.getValue(), setBackground);
            }
        });

        radioButton3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background3.getValue(), setBackground);
            }
        });

        radioButton4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background4.getValue(), setBackground);
            }
        });

        radioButton5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background5.getValue(), setBackground);
            }
        });

        radioButton6.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBackgroundState(activity, Backgrounds.Background6.getValue(), setBackground);
            }
        });
        // endregion

        return new RadioButton[]{radioButton0, radioButton1, radioButton2, radioButton3, radioButton4, radioButton5, radioButton6};
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static void saveBackgroundState(Activity activity, int value, LinearLayout setBackground) {
        saveSP(activity, "background", value);
        TransitUser.user.getOtherInfo().setPreferBackground(value);

        background = value;

        setBackground.setBackground(activity.getResources().getDrawable(value, activity.getTheme()));
    }

    public static void saveSP(Activity activity, String key, int value) {
        com.example.instagram.services.SharedPreferences.saveSP(activity, key, value);
    }

    public static int loadSP(Activity activity, String key) {
        return com.example.instagram.services.SharedPreferences.loadIntSP(activity, key);
    }

    public static void deleteSp(Activity activity, String key) {
        com.example.instagram.services.SharedPreferences.deleteSP(activity, key);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void loadBackground(Activity activity, LinearLayout linearLayout) { // load bg after resume activity
        int idBackground = loadSP(activity, "background");

        linearLayout.setBackground(idBackground != 0
                ? activity.getResources().getDrawable(idBackground, activity.getTheme())
                : activity.getResources().getDrawable(R.drawable.gradient_list_main, activity.getTheme()));

        saveBackgroundState(activity, idBackground == 0 ? Backgrounds.Background0.getValue() : idBackground, linearLayout);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setThemeContent(Resources resources, ImageView imageView, Context context) {
        imageView.setImageDrawable(ThemesBackgrounds.isNight(resources)
                ? resources.getDrawable(R.drawable.sun, context.getTheme())
                : resources.getDrawable(R.drawable.moon, context.getTheme()));
    }
}

package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;
import com.google.zxing.WriterException;

import java.util.Locale;

public class Settings {
    public static AlertDialog.Builder getSettingsMenu(Activity activity) throws WriterException {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(activity).inflate(R.layout.settings, null, false);
        LinearLayout layout = view.findViewById(R.id.settings);
        ThemesBackgrounds.loadBackground(activity, layout);

        setLanguages(view, activity);

        return new AlertDialog.Builder(activity).setCancelable(false).setView(view).setPositiveButton(activity.getApplicationContext().getString(R.string.permission_ok), (dialog, which) -> activity.recreate());
    }

    private static void setLanguages(View view, Activity activity) {
        Spinner languages = view.findViewById(R.id.languages);
        languages.setAdapter(Localisation.getAdapter(activity));

        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = ((TextView) view).getText().toString();
                Configuration configuration = Localisation.setLocalize(Localisation.getLocales().get(name));
                activity.getResources().updateConfiguration(configuration, activity.getResources().getDisplayMetrics());
                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        languages.setOnItemSelectedListener(itemLocaliseSelectedListener);
    }
}

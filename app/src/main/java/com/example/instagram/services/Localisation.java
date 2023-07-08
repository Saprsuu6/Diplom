package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Localisation {
    private static Map<String, Locale> locales;

    public static Configuration setLocalize(Locale locale) {
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        return configuration;
    }

    public static ArrayAdapter<String> getAdapter(Activity activity) {
        ArrayAdapter<String> adapter;
        String[] languagesArray = {activity.getString(R.string.en), activity.getString(R.string.uk)};

        locales = new HashMap<>();
        locales.put("English", new Locale("en", "EN"));
        locales.put("Українська", new Locale("uk", "UK"));

        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, languagesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    public static AlertDialog.Builder getLanguagesMenu(Activity activity) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(activity).inflate(R.layout.settings, null, false);
        LinearLayout layout = view.findViewById(R.id.settings);
        ThemesBackgrounds.loadBackground(activity, layout);

        Spinner languages = view.findViewById(R.id.languages);
        languages.setAdapter(Localisation.getAdapter(activity));

        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = ((TextView) view).getText().toString();
                Configuration configuration = Localisation.setLocalize(locales.get(name));
                activity.getResources().updateConfiguration(configuration, activity.getResources().getDisplayMetrics());
                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        languages.setOnItemSelectedListener(itemLocaliseSelectedListener);

        return new AlertDialog.Builder(activity).setCancelable(false).setView(view).setPositiveButton(activity.getApplicationContext().getString(R.string.permission_ok), (dialog, which) -> activity.recreate());
    }
}

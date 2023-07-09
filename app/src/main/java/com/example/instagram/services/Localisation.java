package com.example.instagram.services;

import android.app.Activity;
import android.content.res.Configuration;
import android.widget.ArrayAdapter;

import com.example.instagram.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Localisation {
    public static Map<String, Locale> getLocales() {
        return locales;
    }

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
}

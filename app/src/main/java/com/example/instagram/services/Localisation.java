package com.example.instagram.services;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.instagram.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Localisation {
    public Map<String, Locale> getLocales() {
        return locales;
    }

    private final Map<String, Locale> locales;
    public static Locale chosenLocale;
    private final ArrayAdapter<String> adapter;

    public Localisation(Activity activity) {
        String[] languagesArray = {activity.getString(R.string.en),
                activity.getString(R.string.uk)};

        locales = new HashMap<>();
        locales.put("English", new Locale("en", "EN"));
        locales.put("Українська", new Locale("uk", "UK"));

        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, languagesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public static void setFirstLocale(Spinner languages) {
        if (Localisation.chosenLocale != null) {
            if (Localisation.chosenLocale.toString().equals("en_EN")) {
                languages.setSelection(0);
            } else if (Localisation.chosenLocale.toString().equals("uk_UK")) {
                languages.setSelection(1);
            }
        }
    }

    public static Configuration setLocalize(AdapterView<?> parent, Localisation localisation, int position) {
        String language = (String) parent.getItemAtPosition(position);

        Locale locale = Objects.requireNonNull(localisation.getLocales().get(language));
        Locale.setDefault(locale);

        Localisation.chosenLocale = locale;

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        return configuration;
    }

    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }
}

package com.example.instagram.services;

import android.app.Activity;
import android.content.Context;

public class Cache {
    public static void saveSP(Activity activity, String key, Object obj) {
        android.content.SharedPreferences preferences = activity.getSharedPreferences(CacheScopes.MAIN_SCOPE.toString(), Context.MODE_PRIVATE);

        android.content.SharedPreferences.Editor editor = preferences.edit();

        if (obj.getClass().equals(Boolean.class)) {
            editor.putBoolean(key, (Boolean) obj);
        } else if (obj.getClass().equals(Integer.class)) {
            editor.putInt(key, (Integer) obj);
        } else if (obj.getClass().equals(String.class)) {
            editor.putString(key, (String) obj);
        }

        editor.apply();
    }

    public static void deleteAppSP(Activity activity) {
        android.content.SharedPreferences sharedPreferences = activity.getSharedPreferences(CacheScopes.MAIN_SCOPE.toString(), Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    public static void deleteSP(Activity activity, String key) {
        android.content.SharedPreferences sharedPreferences = activity.getSharedPreferences(CacheScopes.MAIN_SCOPE.toString(), Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key).apply();
    }

    public static boolean loadBoolSP(Activity activity, String key) {
        android.content.SharedPreferences sharedPreferences = activity.getSharedPreferences(CacheScopes.MAIN_SCOPE.toString(), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static int loadIntSP(Activity activity, String key) {
        android.content.SharedPreferences sharedPreferences = activity.getSharedPreferences(CacheScopes.MAIN_SCOPE.toString(), Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static String loadStringSP(Activity activity, String key) {
        android.content.SharedPreferences sharedPreferences = activity.getSharedPreferences(CacheScopes.MAIN_SCOPE.toString(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
}

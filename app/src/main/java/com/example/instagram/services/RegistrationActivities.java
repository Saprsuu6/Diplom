package com.example.instagram.services;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivities {
    public static List<Activity> activityList = new ArrayList<>();

    public static void deleteActivities() {
        for (Activity activity : activityList) {
            activity.finish();
        }

        activityList.clear();
    }
}

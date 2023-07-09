package com.example.instagram.services;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class Permissions {
    public static int STORAGE_PERMISSION_CODE = 1;
    private static final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA};

    public static boolean isExternalStoragePermissionGranted(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean checkPermissions(Activity activity) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static void setPermissions(Activity activity) {
        if (!checkPermissions(activity)) {
            ActivityCompat.requestPermissions(activity, permissions, Permissions.STORAGE_PERMISSION_CODE);
        }
    }
}

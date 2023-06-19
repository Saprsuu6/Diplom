package com.example.instagram.services;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.Objects;

public class DeleteApplicationCache {
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            boolean temp = deleteDir(dir);
            Log.d("deleteCache: ", String.valueOf(temp));
        } catch (Exception e) {
            Log.e("deleteCache: ", e.getMessage());
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < Objects.requireNonNull(children).length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}

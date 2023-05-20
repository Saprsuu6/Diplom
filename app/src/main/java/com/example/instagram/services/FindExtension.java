package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class FindExtension {
    @SuppressLint("Range")
    public static String getExtension(Uri selectedImageUri, Context context) {
        String fileName = null;
        if (selectedImageUri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(selectedImageUri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (fileName == null){
            fileName = selectedImageUri.getPath();
            int mark = fileName.lastIndexOf("/");
            if (mark != -1){
                fileName = fileName.substring(mark + 1);
            }
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}

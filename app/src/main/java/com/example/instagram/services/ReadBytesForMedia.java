package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;

public class ReadBytesForMedia {
    public static byte[] readBytes(Context context, Uri uri) throws IOException {
        @SuppressLint("Recycle") InputStream imageStream = context.getContentResolver().openInputStream(uri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return imageStream.readAllBytes();
        } else {
            return null;
        }
    }
}

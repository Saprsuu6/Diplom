package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadBytesForMedia {
    public static byte[] readBytes(Context context, Uri uri, float size) throws IOException {
        @SuppressLint("Recycle") InputStream stream = context.getContentResolver().openInputStream(uri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return stream.readAllBytes();
        } else {
            return getBytes(stream, size);
        }
    }

    public static byte[] getBytes(InputStream inputStream, float size) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[(int) size];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}

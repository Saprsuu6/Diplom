package com.example.instagram.services;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class GetFileInfo {
    public static long getSize(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.SIZE}, null, null, null);
        if (cursor != null) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            cursor.moveToFirst();

            long size = cursor.getLong(sizeIndex);
            cursor.close();
            return size;
        }

        return 0;
    }

    public static String getName(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();

            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }

        return null;
    }

    public static String[] splitFileName(@NonNull String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    public static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old " + newName + " file");
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[1024 * 4];
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static File loadFromUrl(URL url) throws IOException {
        File target = File.createTempFile("file", ".mp4"); // TODO make dinamic
        new Thread(() -> {
            try (InputStream in = url.openStream();
                 BufferedInputStream bis = new BufferedInputStream(in);
                 FileOutputStream fos = new FileOutputStream(target)) {
                byte[] data = new byte[1024];
                int count;
                while ((count = bis.read(data, 0, 1024)) != -1) {
                    fos.write(data, 0, count);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }).start();

        return target;
    }
}

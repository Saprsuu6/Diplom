package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;

public class OpenMedia {
    @SuppressLint("IntentReset")
    public static void openGallery(Activity activity, MediaTypes type, ActivityResultLauncher<Intent> someActivityResultLauncher) {
        boolean permission = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (type == MediaTypes.IMAGE) permission = Permissions.isReadImageGranted(activity);
            else if (type == MediaTypes.VIDEO)
                permission = Permissions.isReadVideoGranted(activity);
            else if (type == MediaTypes.AUDIO)
                permission = Permissions.isReadAudioGranted(activity);
        }


        if (permission) {
            Intent intent;

            if (type == MediaTypes.IMAGE) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
            } else if (type == MediaTypes.VIDEO) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
            } else {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("audio/*");
            }

            intent.setAction(Intent.ACTION_GET_CONTENT);

            someActivityResultLauncher.launch(intent);
        } else {
            Permissions.setPermissions(activity);
        }
    }
}

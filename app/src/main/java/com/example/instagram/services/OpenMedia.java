package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;

public class OpenMedia {
    @SuppressLint("IntentReset")
    public static void openGallery(Context context, MediaTypes type, ActivityResultLauncher<Intent> someActivityResultLauncher) {
        if (Cache.loadBoolSP(context, CacheScopes.MEDIA_PERMISSION.toString())) {
            Intent intent = null;

            if (type == MediaTypes.IMAGE) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
            } else if (type == MediaTypes.VIDEO) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
            } else if (type == MediaTypes.AUDIO) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("audio/*");
            }

            if (intent != null) intent.setAction(Intent.ACTION_GET_CONTENT);

            someActivityResultLauncher.launch(intent);
        } else {
            AlertDialog.Builder permissionsDialog = Permissions.getPermissionMediaDialog(context, context.getResources());
            permissionsDialog.setNegativeButton("Cancel", null);
            permissionsDialog.create().show();
        }
    }
}

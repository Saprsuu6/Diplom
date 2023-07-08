package com.example.instagram.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.instagram.R;

public class Permissions {
    public static AlertDialog.Builder getPermissionDialog(Context context, Resources resources) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(40, 0, 40, 0);
        linearLayout.setGravity(Gravity.CENTER);

        // TODO add phone book
        CheckBox mediaCheckBox = mediaPermission(context, resources);
        linearLayout.addView(mediaCheckBox);

        return new AlertDialog.Builder(context)
                .setMessage(resources.getString(R.string.permission)) // set Locale Resources
                .setCancelable(false)
                .setView(linearLayout)
                .setPositiveButton(resources.getString(R.string.permission_ok), (dialog1, which) -> Cache.saveSP(context, CacheScopes.MEDIA_PERMISSION.toString(), mediaCheckBox.isChecked()));
    }

    public static AlertDialog.Builder getPermissionMediaDialog(Context context, Resources resources) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(40, 0, 40, 0);
        linearLayout.setGravity(Gravity.CENTER);

        CheckBox mediaCheckBox = mediaPermission(context, resources);

        linearLayout.addView(mediaCheckBox);

        return new AlertDialog.Builder(context)
                .setMessage(resources.getString(R.string.media_permission)) // set Locale Resources
                .setCancelable(false)
                .setView(linearLayout)
                .setPositiveButton(resources.getString(R.string.permission_ok), (dialog1, which) -> Cache.saveSP(context, CacheScopes.MEDIA_PERMISSION.toString(), mediaCheckBox.isChecked()));
    }

    private static CheckBox mediaPermission(Context context, Resources resources) {
        CheckBox mediaPermission = new CheckBox(context);
        mediaPermission.setText(resources.getString(R.string.media_permission));

        return mediaPermission;
    }
}

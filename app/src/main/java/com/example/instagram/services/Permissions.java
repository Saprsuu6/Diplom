package com.example.instagram.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.instagram.R;

public class Permissions {
    public static AlertDialog.Builder getPermissionDialog(Context context, Resources resources) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(40, 0, 40, 0);

        CheckBox phoneBookCheckBox = getPhoneBookPermission(context, resources);
        CheckBox mediaCheckBox = mediaPermission(context, resources);

        linearLayout.addView(phoneBookCheckBox);
        linearLayout.addView(mediaCheckBox);

        return new AlertDialog.Builder(context)
                .setMessage(resources.getString(R.string.permission)) // set Locale Resources
                .setCancelable(false)
                .setView(linearLayout)
                .setPositiveButton(resources.getString(R.string.permission_ok), (dialog1, which) -> {
                    TransitUser.user.getOtherInfo().setPhoneBookPermission(phoneBookCheckBox.isChecked());
                    TransitUser.user.getOtherInfo().setMediaPermission(mediaCheckBox.isChecked());
                });
    }

    public static AlertDialog.Builder getPermissionMediaDialog(Context context, Resources resources) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(40, 0, 40, 0);

        CheckBox mediaCheckBox = mediaPermission(context, resources);

        linearLayout.addView(mediaCheckBox);

        return new AlertDialog.Builder(context)
                .setMessage(resources.getString(R.string.media_permission)) // set Locale Resources
                .setCancelable(false)
                .setView(linearLayout)
                .setPositiveButton(resources.getString(R.string.permission_ok), (dialog1, which) -> {
                    TransitUser.user.getOtherInfo().setMediaPermission(mediaCheckBox.isChecked());
                });
    }

    private static CheckBox getPhoneBookPermission(Context context, Resources resources) {
        CheckBox phoneBookPermission = new CheckBox(context);
        phoneBookPermission.setText(resources.getString(R.string.phone_book_permission));
        phoneBookPermission.setChecked(true);
        phoneBookPermission.setFocusable(false);
        phoneBookPermission.setClickable(false);

        return phoneBookPermission;
    }

    private static CheckBox mediaPermission(Context context, Resources resources) {
        CheckBox mediaPermission = new CheckBox(context);
        mediaPermission.setText(resources.getString(R.string.media_permission));

        return mediaPermission;
    }
}

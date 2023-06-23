package com.example.instagram.services;

import android.content.Context;
import android.widget.Toast;

import com.example.instagram.R;

public class Errors {
    public static Toast registrationUser(Context context, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(context, R.string.add_user_0, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(context, R.string.add_user_1, Toast.LENGTH_SHORT);
        else if (responseStr.contains("2"))
            return Toast.makeText(context, R.string.add_user_2, Toast.LENGTH_SHORT);
        else if (responseStr.contains("3"))
            return Toast.makeText(context, R.string.add_user_3, Toast.LENGTH_SHORT);
        else if (responseStr.contains("4"))
            return Toast.makeText(context, R.string.add_user_4, Toast.LENGTH_SHORT);
        else
            return Toast.makeText(context, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast authoriseUser(Context context, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(context, R.string.authorise_0, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(context, R.string.authorise_1, Toast.LENGTH_SHORT);
        else if (responseStr.contains("2"))
            return Toast.makeText(context, R.string.authorise_2, Toast.LENGTH_SHORT);
        else if (responseStr.contains("3"))
            return Toast.makeText(context, R.string.authorise_3, Toast.LENGTH_SHORT);
        else
            return Toast.makeText(context, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast forgotPasswordCode(Context context, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(context, R.string.forgot_password_code_0, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(context, R.string.forgot_password_code_1, Toast.LENGTH_SHORT);
        else
            return Toast.makeText(context, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast afterRestorePassword(Context context, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(context, R.string.after_restore_0, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(context, R.string.after_restore_1, Toast.LENGTH_SHORT);
        else if (responseStr.contains("2"))
            return Toast.makeText(context, R.string.after_restore_2, Toast.LENGTH_SHORT);
        else if (responseStr.contains("3"))
            return Toast.makeText(context, R.string.after_restore_3, Toast.LENGTH_SHORT);
        else if (responseStr.contains("4"))
            return Toast.makeText(context, R.string.after_restore_4, Toast.LENGTH_SHORT);
        else if (responseStr.contains("5"))
            return Toast.makeText(context, R.string.after_restore_5, Toast.LENGTH_SHORT);
        else
            return Toast.makeText(context, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast enterCode(Context context, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(context, R.string.enter_code_0, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(context, R.string.enter_code_1, Toast.LENGTH_SHORT);
        else if (responseStr.contains("2"))
            return Toast.makeText(context, R.string.enter_code_2, Toast.LENGTH_SHORT);
        else
            return Toast.makeText(context, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast sendAvatar(Context context, String responseStr) {
        if (responseStr.equals("0")) {
            return Toast.makeText(context, R.string.successfully_loaded_0, Toast.LENGTH_SHORT);
        } else {
            return Toast.makeText(context, R.string.unsuccessfully_loaded_1, Toast.LENGTH_SHORT);
        }
    }

    public static Toast delete(Context context, String responseStr) {
        if (responseStr.equals("0")) {
            return Toast.makeText(context, R.string.successful_delete, Toast.LENGTH_SHORT);
        } else {
            return Toast.makeText(context, R.string.unsuccessful_delete, Toast.LENGTH_SHORT);
        }
    }
}

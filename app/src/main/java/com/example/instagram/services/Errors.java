package com.example.instagram.services;

import android.app.Activity;
import android.widget.Toast;

import com.example.instagram.R;

public class Errors {
    public static Toast registrationUser(Activity activity, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(activity, R.string.add_user_0, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(activity, R.string.add_user_1, Toast.LENGTH_SHORT);
        else if (responseStr.contains("2"))
            return Toast.makeText(activity, R.string.add_user_2, Toast.LENGTH_SHORT);
        else if (responseStr.contains("3"))
            return Toast.makeText(activity, R.string.add_user_3, Toast.LENGTH_SHORT);
        else if (responseStr.contains("4"))
            return Toast.makeText(activity, R.string.add_user_4, Toast.LENGTH_SHORT);
        else return Toast.makeText(activity, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast authoriseUser(Activity activity, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(activity, R.string.authorise_0, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(activity, R.string.authorise_1, Toast.LENGTH_SHORT);
        else if (responseStr.contains("2"))
            return Toast.makeText(activity, R.string.authorise_2, Toast.LENGTH_SHORT);
        else if (responseStr.contains("3"))
            return Toast.makeText(activity, R.string.authorise_3, Toast.LENGTH_SHORT);
        else return Toast.makeText(activity, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast forgotPasswordCode(Activity activity, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(activity, R.string.forgot_password_code_0, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(activity, R.string.forgot_password_code_1, Toast.LENGTH_SHORT);
        else return Toast.makeText(activity, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast afterRestorePassword(Activity activity, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(activity, R.string.after_restore_0, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(activity, R.string.after_restore_1, Toast.LENGTH_SHORT);
        else if (responseStr.contains("2"))
            return Toast.makeText(activity, R.string.after_restore_2, Toast.LENGTH_SHORT);
        else if (responseStr.contains("3"))
            return Toast.makeText(activity, R.string.after_restore_3, Toast.LENGTH_SHORT);
        else if (responseStr.contains("4"))
            return Toast.makeText(activity, R.string.after_restore_4, Toast.LENGTH_SHORT);
        else if (responseStr.contains("5"))
            return Toast.makeText(activity, R.string.after_restore_5, Toast.LENGTH_SHORT);
        else return Toast.makeText(activity, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast enterCode(Activity activity, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(activity, R.string.enter_code_0, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(activity, R.string.enter_code_1, Toast.LENGTH_SHORT);
        else if (responseStr.contains("2"))
            return Toast.makeText(activity, R.string.enter_code_2, Toast.LENGTH_SHORT);
        else return Toast.makeText(activity, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast sendAvatar(Activity activity, String responseStr) {
        if (responseStr.equals("0")) {
            return Toast.makeText(activity, R.string.unsuccessfully_loaded_1, Toast.LENGTH_SHORT);
        } else {
            return Toast.makeText(activity, R.string.successfully_loaded_0, Toast.LENGTH_SHORT);
        }
    }

    public static Toast delete(Activity activity, String responseStr) {
        if (responseStr.equals("0")) {
            return Toast.makeText(activity, R.string.successful_delete, Toast.LENGTH_SHORT);
        } else {
            return Toast.makeText(activity, R.string.unsuccessful_delete, Toast.LENGTH_SHORT);
        }
    }

    public static Toast emailCodes(Activity activity, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(activity, R.string.successful_user_update, Toast.LENGTH_SHORT);
        else if (responseStr.contains("1"))
            return Toast.makeText(activity, R.string.incorrect_code_1, Toast.LENGTH_SHORT);
        else if (responseStr.contains("2"))
            return Toast.makeText(activity, R.string.incorrect_token_2, Toast.LENGTH_SHORT);
        else if (responseStr.contains("3"))
            return Toast.makeText(activity, R.string.empty_3, Toast.LENGTH_SHORT);
        else if (responseStr.contains("4"))
            return Toast.makeText(activity, R.string.match_4, Toast.LENGTH_SHORT);
        else return Toast.makeText(activity, R.string.internal_error, Toast.LENGTH_SHORT);
    }

    public static Toast editProfile(Activity activity, String responseStr) {
        if (responseStr.contains("0"))
            return Toast.makeText(activity, R.string.successful_user_update, Toast.LENGTH_SHORT);
        else if (responseStr.contains("3"))
            return Toast.makeText(activity, R.string.email_are_not_valid, Toast.LENGTH_SHORT);
        else return Toast.makeText(activity, R.string.internal_error, Toast.LENGTH_SHORT);
    }
}

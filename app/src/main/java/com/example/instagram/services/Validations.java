package com.example.instagram.services;

import android.content.res.Resources;

import com.example.instagram.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validations {
    public static void validatePhoneNumber(String text, String countryCode, Resources resources) throws Exception {
        String[] numbersArray = text.split("\\D+");
        StringBuilder phoneNumber = new StringBuilder(String.join("", numbersArray));
        phoneNumber.insert(0, countryCode);

        Validator.matcherPhone = Validator.phone.matcher(phoneNumber.toString());
        if (!Validator.matcherPhone.find()) {
            if (phoneNumber.length() > 13) {
                throw new Exception(resources.getString(R.string.phone_number_error_length));
            } else {
                throw new Exception(resources.getString(R.string.phone_number_error_values));
            }
        }
    }

    public static void validateUserName(String text, Resources resources) throws Exception {
        Validator.matcherUserName = Validator.userName.matcher(text);
        if (!Validator.matcherUserName.find()) {
            if (text.length() < 1 || text.length() > 20) {
                throw new Exception(resources.getString(R.string.username_error_length));
            } else {
                throw new Exception(resources.getString(R.string.username_error_length)
                        + ". " + resources.getString(R.string.username_error_spec_symbols));
            }
        }
    }

    public static void validateEmail(String text, String emailName, Resources resources) throws Exception {
        String email = text + emailName;

        Validator.matcherEmail = Validator.email.matcher(email);
        if (!Validator.matcherEmail.find()) {
            throw new Exception(resources.getString(R.string.email_error_spec_symbols));
        }
    }

    public static void validatePassword(String text, Resources resources) throws Exception {
        Validator.matcherPassword = Validator.password.matcher(text);
        if (!Validator.matcherPassword.find()) {
            Pattern specSymbols = Pattern.compile("[@$!%*?&]");
            Matcher matcherSpecSymbols = specSymbols.matcher(text);
            if (text.length() < 8 || text.length() > 32) {
                throw new Exception(resources.getString(R.string.password_error_length));
            } else if (!matcherSpecSymbols.find()) {
                throw new Exception(resources.getString(R.string.password_error_spec_symbols));
            } else {
                throw new Exception(resources.getString(R.string.password_error_letters)
                        + "\t" + resources.getString(R.string.password_error_numbers));
            }
        }
    }
}

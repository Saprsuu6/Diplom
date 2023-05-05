package com.example.instagram.services;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Validator implements TextWatcher {
    private final EditText editText;
    public static Pattern phone = Pattern.compile("^\\+380\\d{3}\\d{2}\\d{2}\\d{2}$");
    public static Pattern userName = Pattern.compile("^(?=.*[A-Za-z\\d]$)[A-Za-z][A-Za-z\\d.-]{0,19}$");
    public static Pattern email = Pattern.compile("^([a-zA-Z\\d_\\-.]+)@([a-zA-Z\\d_\\-.]+)\\.([a-zA-Z]{2,5})$");
    public static Pattern password = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$");
    public static Matcher matcherPhone;
    public static Matcher matcherUserName;
    public static Matcher matcherEmail;
    public static Matcher matcherPassword;

    public Validator(EditText editText) {
        this.editText = editText;
    }

    public abstract void validate(EditText editText, String text);

    @Override
    public void afterTextChanged(Editable s) {
        String text = editText.getText().toString();
        validate(editText, text);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}

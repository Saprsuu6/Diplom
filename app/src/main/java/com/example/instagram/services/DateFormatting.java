package com.example.instagram.services;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatting {
    @SuppressLint("SimpleDateFormat")
    public static void setSimpleDateFormat(String locale) {
        switch (locale) {
            case "EN":
                simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                break;
            case "UKR":
                simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                break;
        }
    }

    public static String formatToGeneralDate(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormatGeneral =
                new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormatGeneral.format(date.getTime());
    }

    private static SimpleDateFormat simpleDateFormat;

    public static String formatDate(Calendar date) {
        return simpleDateFormat.format(date.getTime());
    }
}

package com.example.instagram.services;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DateFormatting {
    @SuppressLint("SimpleDateFormat")
    public static void setSimpleDateFormat(String locale) {
        switch (locale) {
            case "EN":
                simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                break;
            case "UK":
                simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat simpleDateFormat;

    public static String formatDate(Calendar date) {
        return simpleDateFormat.format(date.getTime());
    }

    public static String formatDate(Date date) {
        return simpleDateFormat.format(date.getTime());
    }

    public static Date formatDate(String date) throws ParseException {
        return simpleDateFormat.parse(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static Date formatDateFromStandard(@NonNull String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
    }

    public static Calendar getCalendar(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String formatToDateWithoutTime(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormatGeneral =
                new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormatGeneral.format(date.getTime());
    }

    public static String formatToDateWithTime(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormatGeneral =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormatGeneral.format(date.getTime());
    }

}

package com.example.instagram.services;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatting {
    @SuppressLint("SimpleDateFormat")
    public static void setSimpleDateFormat(String locale) {
        if ("UK".equals(locale)) {
            simpleDateFormatWithoutTime = new SimpleDateFormat("dd.MM.yyyy");
            simpleDateFormatWithTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        } else { // EN, US
            simpleDateFormatWithoutTime = new SimpleDateFormat("dd MMMM yyyy");
            simpleDateFormatWithTime = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat simpleDateFormatWithoutTime;

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat simpleDateFormatWithTime;

    public static String formatDateWithTime(Calendar date) {
        return simpleDateFormatWithTime.format(date.getTime());
    }

    public static String formatDate(Calendar date) {
        return simpleDateFormatWithoutTime.format(date.getTime());
    }

    public static String formatDate(Date date) {
        return simpleDateFormatWithoutTime.format(date.getTime());
    }

    public static Date formatDate(String date) throws ParseException {
        return simpleDateFormatWithoutTime.parse(date);
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

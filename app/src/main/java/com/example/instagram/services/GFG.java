package com.example.instagram.services;

import java.util.concurrent.TimeUnit;

public class GFG {
    public static String convert(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = (TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60);

        // Print the answer
        return String.format(minutes + ":" + seconds);
    }
}

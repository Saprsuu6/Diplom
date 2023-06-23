package com.example.instagram.services.pagination;

public class PaginationCurrentForAllPosts {
    public static final int amountOfPagination = 5;
    public static int current = 0;

    public static void nextCurrent() {
        current += amountOfPagination;
    }

    public static void resetCurrent() {
        current = 0;
    }

    // TODO save pagination current
}

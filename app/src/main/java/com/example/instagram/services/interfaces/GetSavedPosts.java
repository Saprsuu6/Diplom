package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetSavedPosts {
    @GET("getSavedPosts")
    Call<String> STRING_CALL(@Query("from") String from, @Query("amount") String amount, @Query("login") String login);
}

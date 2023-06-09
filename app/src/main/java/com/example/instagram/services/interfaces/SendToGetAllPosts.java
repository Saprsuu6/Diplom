package com.example.instagram.services.interfaces;

import androidx.annotation.Nullable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SendToGetAllPosts {
    @GET("/posts")
    Call<String> STRING_CALL(
            @Query("amount") int amount,
            @Nullable @Query("scroll") String scroll
    );
}

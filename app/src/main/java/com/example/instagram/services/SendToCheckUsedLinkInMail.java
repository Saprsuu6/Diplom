package com.example.instagram.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SendToCheckUsedLinkInMail {
    @GET("/confirm")
    Call<String> STRING_CALL(
            @Query("login") String login
    );
}

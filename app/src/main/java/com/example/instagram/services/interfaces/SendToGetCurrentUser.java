package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SendToGetCurrentUser {
    @GET("/Clickshot/getPublicUser")
    Call<String> STRING_CALL(
            @Query("login") String login
    );
}

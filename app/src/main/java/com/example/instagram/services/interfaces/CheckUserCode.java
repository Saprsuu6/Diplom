package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CheckUserCode {
    @GET("/Clickshot/restore")
    Call<String> STRING_CALL(
            @Query("login") String login,
            @Query("code") String code
    );
}

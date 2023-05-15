package com.example.instagram.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SendToForgotPassword {
    @POST("/confirm")
    Call<String> STRING_CALL(
            @Body String user
    );
}

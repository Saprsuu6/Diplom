package com.example.instagram.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SendNewPasswordAfterForgot {
    @POST("/restore")
    Call<String> STRING_CALL (
            @Body String info
    );
}

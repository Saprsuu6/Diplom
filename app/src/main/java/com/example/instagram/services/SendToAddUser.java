package com.example.instagram.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SendToAddUser {
    @POST("/add")
    Call<String> STRING_CALL(
            @Body String info
    );
}

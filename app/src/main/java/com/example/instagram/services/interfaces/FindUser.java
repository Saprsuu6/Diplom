package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FindUser {
    @POST("findUser")
    Call<String> STRING_CALL(@Body String body);
}

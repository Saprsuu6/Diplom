package com.example.instagram.services.interfaces;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SendAva {
    @retrofit2.http.Multipart
    @POST("/ava")
    Call<ResponseBody> STRING_CALL(
            @Part("ava") RequestBody file,
            @Part("login") RequestBody login
    );
}

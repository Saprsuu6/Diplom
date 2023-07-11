package com.example.instagram.services.interfaces;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NewAva {
    @retrofit2.http.Multipart
    @POST("ava")
    Call<String> STRING_CALL(@Part("ava") RequestBody file, @Part("login") String login);
}

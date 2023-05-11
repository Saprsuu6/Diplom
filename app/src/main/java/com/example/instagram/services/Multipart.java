package com.example.instagram.services;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Multipart {
    @retrofit2.http.Multipart
    @POST("index.php") // TODO change way
    Call<String> UPLOAD(
            @Part("extension") String extension,
            @Part("image") String image
    );
}

package com.example.instagram.services;

import androidx.annotation.Nullable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Multipart {
    @retrofit2.http.Multipart
    @POST("/image/*")
    Call<ResponseBody> STRING_CALL(
            @Part("image") RequestBody file,
            @Nullable @Part("description") String description
    );
}

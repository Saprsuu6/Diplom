package com.example.instagram.services.interfaces;

import androidx.annotation.Nullable;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SendToAddPost {
    @retrofit2.http.Multipart
    @POST("/addPost")
    Call<ResponseBody> STRING_CALL(
            @Part("image") RequestBody file,
            @Nullable @Part("otherInfo") String description
    );
}

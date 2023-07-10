package com.example.instagram.services.interfaces;

import androidx.annotation.Nullable;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AddPost {
    @Multipart
    @POST("/Clickshot/addPost")
    Call<String> STRING_CALL(@Part("media") RequestBody file, @Nullable @Part("otherInfo") String body);
}

package com.example.instagram.services;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SendPhotoBytes {
    @retrofit2.http.Multipart
    @POST("/image/*")
    Call<ResponseBody> STRING_CALL(
            @Part MultipartBody.Part image
    );
}

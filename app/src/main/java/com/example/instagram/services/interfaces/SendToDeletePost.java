package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SendToDeletePost {
    @POST("/deletePost")
    Call<String> STRING_CALL (
            @Body String postId
    );
}

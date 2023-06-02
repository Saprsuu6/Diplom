package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SendToGetAllPosts {
    @GET("/posts") // TODO decide with pagination
    Call<String> STRING_CALL(
//            @Query("page") int page,
//            @Query("limit") int limitOfOnePage
    );
}

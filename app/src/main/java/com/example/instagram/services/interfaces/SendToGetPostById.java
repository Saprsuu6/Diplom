package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SendToGetPostById {
    @GET("/Clickshot/getPostById")
    Call<String> STRING_CALL (
            @Query("postId") String posId
    );
}

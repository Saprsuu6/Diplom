package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetComments {
    @GET("/Clickshot/comments")
    Call<String> STRING_CALL(
            @Query("postId") int postId,
            @Query("amount") int amount
    );
}

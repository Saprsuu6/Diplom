package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SendToGetPostsOfUser {
    @GET("/Clickshot/postsOfUser")
    Call<String> STRING_CALL(
            @Query("from") int from,
            @Query("amount") int amount,
            @Query("login") String login
    );
}

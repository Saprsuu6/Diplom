package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetTaggedPeople {
    @GET("/Clickshot/taggedPeople")
    Call<String> STRING_CALL (
            @Query("postId") String postId
    );
}

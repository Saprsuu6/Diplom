package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetCommentById {
    @GET("/Clickshot/getCommentById")
    Call<String> STRING_CALL(
            @Query("commentId") String commentId
    );
}

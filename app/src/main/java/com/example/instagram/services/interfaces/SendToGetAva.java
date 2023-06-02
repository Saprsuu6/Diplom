package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SendToGetAva {
    @GET("/ava")
    Call<String> STRING_CALL (
            @Query("login") String nickName
    );
}

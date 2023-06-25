package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SendToChangeEmailCode {
    @GET("changeEmail")
    Call<String> STRING_CALL(
            @Query("token") String token,
            @Query("code") String code
    );
}

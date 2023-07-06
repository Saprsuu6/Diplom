package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SendToCheckExistUser {
    @POST("/Clickshot/authorize")
    Call<String> STRING_CALL(
            @Body String info
    );
}

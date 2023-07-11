package com.example.instagram.services.interfaces;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChangeEmailFinally {
    @POST("changeEmail")
    Call<String> STRING_CALL(
            @Body String body
    );
}

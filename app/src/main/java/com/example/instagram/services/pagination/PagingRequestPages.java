package com.example.instagram.services.pagination;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PagingRequestPages {
    @GET("/v2/list") // TODO delete
    Call<String> STRING_CALL(
            @Query("page") int page,
            @Query("limit") int limitOfOnePage
    );
}

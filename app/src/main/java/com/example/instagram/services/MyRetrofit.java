package com.example.instagram.services;

import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyRetrofit {
    public static retrofit2.Retrofit initializeRetrofit(String link) {
        return new retrofit2.Retrofit.Builder()
                .baseUrl(link)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }
}

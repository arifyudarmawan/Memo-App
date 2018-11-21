package com.example.pradiptaagus.app_project4.Api;

import android.os.Build;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "http://172.17.100.2:8000/api/v1/";
    public static Retrofit retrofit = null;

    public static Retrofit getApiClient() {
//        OkHttpClient client =
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

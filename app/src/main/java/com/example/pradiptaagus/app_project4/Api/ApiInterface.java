package com.example.pradiptaagus.app_project4.Api;

import com.example.pradiptaagus.app_project4.Model.UserLogin;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login")
    Call<UserLogin> login(
            @Field("email") String email,
            @Field("password") String password
    );
}

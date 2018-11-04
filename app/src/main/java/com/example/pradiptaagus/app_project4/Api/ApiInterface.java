package com.example.pradiptaagus.app_project4.Api;

import com.example.pradiptaagus.app_project4.Model.LoginResponse;
import com.example.pradiptaagus.app_project4.Model.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("signup")
    Call<SignUpResponse> signup(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );
}

package com.example.pradiptaagus.app_project4.Api;

import com.example.pradiptaagus.app_project4.Model.LoginResponse;
import com.example.pradiptaagus.app_project4.Model.LogoutResponse;
import com.example.pradiptaagus.app_project4.Model.MemoResponse;
import com.example.pradiptaagus.app_project4.Model.MemoStoreResponse;
import com.example.pradiptaagus.app_project4.Model.SignUpResponse;
import com.example.pradiptaagus.app_project4.Model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    @GET("memo")
    Call<MemoResponse> getAllMemo(
            @Query("token") String token
    );


    @POST("me")
    Call<UserResponse> getUser(
            @Query("token") String token
    );

    @POST("logout")
    Call<LogoutResponse> logout(
            @Query("token") String token
    );

    @FormUrlEncoded
    @POST("memo")
    Call<MemoStoreResponse> storeMemo(
            @Query("token") String token,
            @Field("title") String title,
            @Field("detail") String detail,
            @Field("user_id") int userId
    );
}

package com.example.pradiptaagus.app_project4.Api;

import com.example.pradiptaagus.app_project4.Model.AddFriendResponse;
import com.example.pradiptaagus.app_project4.Model.DeleteMemoResponse;
import com.example.pradiptaagus.app_project4.Model.FriendNumberResponse;
import com.example.pradiptaagus.app_project4.Model.FriendResponse;
import com.example.pradiptaagus.app_project4.Model.IsFriendResponse;
import com.example.pradiptaagus.app_project4.Model.LoginResponse;
import com.example.pradiptaagus.app_project4.Model.LogoutResponse;
import com.example.pradiptaagus.app_project4.Model.MemoResponse;
import com.example.pradiptaagus.app_project4.Model.SearchUserResponse;
import com.example.pradiptaagus.app_project4.Model.ShowMemoResponse;
import com.example.pradiptaagus.app_project4.Model.SignUpResponse;
import com.example.pradiptaagus.app_project4.Model.StoreFcmTokenResponse;
import com.example.pradiptaagus.app_project4.Model.StoreMemoResponse;
import com.example.pradiptaagus.app_project4.Model.UpdateMemoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
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

    @GET("all_memo/{user_id}")
    Call<MemoResponse> getAllMemo(
            @Path("user_id") int user_id,
            @Query("token") String token
    );


    @POST("logout")
    Call<LogoutResponse> logout(
            @Query("token") String token
    );

    @FormUrlEncoded
    @POST("memo")
    Call<StoreMemoResponse> storeMemoWithRecipient(
            @Query("token") String token,
            @Field("title") String title,
            @Field("detail") String detail,
            @Field("user_id") int userId,
            @Field("friend_id[]") List<Integer> friend_id
    );

    @FormUrlEncoded
    @POST("memo")
    Call<StoreMemoResponse> storeMemo(
            @Query("token") String token,
            @Field("title") String title,
            @Field("detail") String detail,
            @Field("user_id") int userId
    );

    @FormUrlEncoded
    @POST("memo/{memo_id}")
    Call<UpdateMemoResponse> updateMemo(
            @Path("memo_id") int memo_id,
            @Query("token") String token,
            @Field("title") String title,
            @Field("detail") String detail,
            @Field("user_id") int user_id
    );


    @GET("memo/{memo_id}")
    Call<ShowMemoResponse> showMemo(
            @Path("memo_id") int memo_id,
            @Query("token") String token
    );

    @DELETE("memo/{memo_id}")
    Call<DeleteMemoResponse> deleteMemo(
            @Path("memo_id") int memo_id,
            @Query("token") String token
    );

    @GET("user/{user_email}")
    Call<SearchUserResponse> findUser(
            @Path("user_email") String user_email,
            @Query("token") String token
    );

    @GET("friends/{user_id}")
    Call<FriendResponse> getAllFriend(
            @Path("user_id") int user_id,
            @Query("token") String token
    );

    @FormUrlEncoded
    @POST("friend")
    Call<AddFriendResponse> addFriend(
            @Query("token") String token,
            @Field("user_id") int user_id,
            @Field("friend_id") int friend_id
    );

    @GET("isfriend/{email}/{user_id}")
    Call<IsFriendResponse> isFriend(
            @Path("email") String email,
            @Path("user_id") int userId,
            @Query("token") String token
    );

    @GET("number_of_friend/{user_id}")
    Call<FriendNumberResponse> numberOfFriend(
            @Path("user_id") int user_id,
            @Query("token") String token
    );

    @FormUrlEncoded
    @POST("store_fcm_token/{user_id}")
    Call<StoreFcmTokenResponse> storeFcmToken(
            @Path("user_id") int user_id,
            @Query("token") String token,
            @Field("fcm_token") String fcm_token
    );


    @GET("shared_memo/{user_id}")
    Call<MemoResponse> getSharedMemo(
            @Path("user_id") int user_id,
            @Query("token") String token
    );
}

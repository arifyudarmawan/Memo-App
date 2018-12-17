package com.example.pradiptaagus.app_project4.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiService;
import com.example.pradiptaagus.app_project4.Model.AddFriendResponse;
import com.example.pradiptaagus.app_project4.Model.IsFriendResponse;
import com.example.pradiptaagus.app_project4.Model.SearchUserResponse;
import com.example.pradiptaagus.app_project4.R;

import retrofit2.Callback;
import retrofit2.Response;

public class SearchFriendActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etKeyWord;
    private Button btnSearch, btnAdd;
    private ImageView ivHide, ivUser;
    private SharedPreferences userPreferences;
    private String token, userEmail;
    private int userId, friendId;
    private TextView tvUsername, tvEmail, tvNotFound;
    private RelativeLayout profil;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search User");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // get view element
        profil = findViewById(R.id.profil);
        etKeyWord = findViewById(R.id.et_keyword);
        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
        btnAdd = findViewById(R.id.btn_add_friend);
        btnAdd.setOnClickListener(this);
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        ivHide = findViewById(R.id.iv_hide);
        ivHide.setOnClickListener(this);
        ivUser = findViewById(R.id.iv_user);
        tvNotFound = findViewById(R.id.tv_not_found);
        progressBar = findViewById(R.id.pb_search_friend);

        //declare shared preference
        userPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE);

        // get token fro shared preference
        token = userPreferences.getString("token", "missing");
        userId = userPreferences.getInt("userId", 0);
        userEmail = userPreferences.getString("userEmail", "missing");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                search(token);
                break;
            case R.id.iv_hide:
                hideElement();
                break;
            case R.id.btn_add_friend:
                addFriend(token, userId, friendId);
                break;
        }
    }

    private void hideBtnAdd() {
        profil.setVisibility(View.INVISIBLE);
        etKeyWord.setText("");
    }

    private void hideElement() {
        ivUser.setVisibility(View.INVISIBLE);
        tvUsername.setVisibility(View.INVISIBLE);
        tvEmail.setVisibility(View.INVISIBLE);
        btnAdd.setVisibility(View.INVISIBLE);
        ivHide.setVisibility(View.INVISIBLE);
    }

    private void showEmelement() {
        ivUser.setVisibility(View.VISIBLE);
        tvUsername.setVisibility(View.VISIBLE);
        tvEmail.setVisibility(View.VISIBLE);
        ivHide.setVisibility(View.VISIBLE);
    }

    private void search(final String token) {
        progressBar.setVisibility(View.VISIBLE);
        // hide element
        tvNotFound.setVisibility(View.GONE);
        hideElement();

        // get user input
        final String keyWord = etKeyWord.getText().toString();

        ApiService apiInterface = ApiClient.getApiClient().create(ApiService.class);
        retrofit2.Call<SearchUserResponse> call = apiInterface.findUser(keyWord, token);
        call.enqueue(new Callback<SearchUserResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SearchUserResponse> call, Response<SearchUserResponse> response) {
                if (response.body().getEmail() != null) {
                    friendId = response.body().getId();

                    tvUsername.setText(response.body().getName());
                    tvEmail.setText(response.body().getEmail());
                    showEmelement();

                    // check if user id friend or not
                    isFriend(keyWord, userId, token);
                } else {
                    progressBar.setVisibility(View.GONE);
                    tvNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SearchUserResponse> call, Throwable t) {
                Toast.makeText(SearchFriendActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addFriend(String token, int userId, int friendId) {
        ApiService apiInterface = ApiClient.getApiClient().create(ApiService.class);
        retrofit2.Call<AddFriendResponse> call = apiInterface.addFriend(token, userId, friendId);
        call.enqueue(new Callback<AddFriendResponse>() {
            @Override
            public void onResponse(retrofit2.Call<AddFriendResponse> call, Response<AddFriendResponse> response) {
                Toast.makeText(SearchFriendActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                btnAdd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(retrofit2.Call<AddFriendResponse> call, Throwable t) {
                Toast.makeText(SearchFriendActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isFriend(final String email, int userId, String token) {
        ApiService apiInterface = ApiClient.getApiClient().create(ApiService.class);
        retrofit2.Call<IsFriendResponse> call = apiInterface.isFriend(email, userId, token);
        call.enqueue(new Callback<IsFriendResponse>() {
            @Override
            public void onResponse(retrofit2.Call<IsFriendResponse> call, Response<IsFriendResponse> response) {
                int result = response.body().getFriendId();

                if (result == 0) {
                    if (!email.equals(userEmail)) {
                        btnAdd.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        btnAdd.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    btnAdd.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<IsFriendResponse> call, Throwable t) {
                Toast.makeText(SearchFriendActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.pradiptaagus.app_project4.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.SearchUserResponse;
import com.example.pradiptaagus.app_project4.Model.UserResponse;
import com.example.pradiptaagus.app_project4.R;

import retrofit2.Callback;
import retrofit2.Response;

public class SearchFriendActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etKeyWord;
    private Button btnSearch;
    private ImageView ivHide;
    private ProgressDialog progressDialog;
    private SharedPreferences userPreferences;
    private String token;
    private TextView tvUsername, tvEmail, tvNotFound;
    private RelativeLayout profil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Friend");
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
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        ivHide = findViewById(R.id.iv_hide);
        ivHide.setOnClickListener(this);
        tvNotFound = findViewById(R.id.tv_not_found);

        //declare shared preference
        userPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE);

        // get token fro shared preference
        token = userPreferences.getString("token", "missing");

        // initiate progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                search();
                break;
            case R.id.iv_hide:
                hide();
                break;
        }
    }

    private void hide() {
        profil.setVisibility(View.INVISIBLE);
        etKeyWord.setText("");
    }

    private void search() {
        progressDialog.show();
        tvNotFound.setVisibility(View.GONE);
        profil.setVisibility(View.GONE);
        // get user input
        String keyWord = etKeyWord.getText().toString();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        retrofit2.Call<SearchUserResponse> call = apiInterface.findUser(keyWord, token);
        call.enqueue(new Callback<SearchUserResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SearchUserResponse> call, Response<SearchUserResponse> response) {
                if (response.body().getEmail() != null) {
                    tvUsername.setText(response.body().getName());
                    tvEmail.setText(response.body().getEmail());
                    profil.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                } else {
                    tvNotFound.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SearchUserResponse> call, Throwable t) {
                Toast.makeText(SearchFriendActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

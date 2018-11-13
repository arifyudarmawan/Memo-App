package com.example.pradiptaagus.app_project4.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.MemoStoreResponse;
import com.example.pradiptaagus.app_project4.R;

import retrofit2.Callback;
import retrofit2.Response;

public class AddMemoActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etMemoTitle;
    private EditText etMemoDetail;
    private String title;
    private String detail;
    private Button btnSave;
    private Button btnDiscard;
    private String token;
    private int userId;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddMemoActivity.this, MainActivity.class));
            }
        });

        // declare share preference
        final SharedPreferences userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);

        // get token from shared preference
        token = userPreference.getString("token", "missing");
        userId = userPreference.getInt("userId", 0);

        // get user input
        etMemoTitle = findViewById(R.id.et_memo_title);
        etMemoDetail = findViewById(R.id.et_memo_detail);

        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        btnDiscard = findViewById(R.id.btn_discard);
        btnDiscard.setOnClickListener(this);

        if (token == "missing") {
            loginActivity();
        }
    }

    private void loginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                storeMemo(token);
                break;
            case R.id.btn_discard:
                //Toast.makeText(this, "discard", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    private void storeMemo(String token) {
        title = etMemoTitle.getText().toString();
        detail = etMemoDetail.getText().toString();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        retrofit2.Call<MemoStoreResponse> call = apiInterface.storeMemo(token, title, detail, userId);
        call.enqueue(new Callback<MemoStoreResponse>() {
            @Override
            public void onResponse(retrofit2.Call<MemoStoreResponse> call, Response<MemoStoreResponse> response) {
                Toast.makeText(AddMemoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(retrofit2.Call<MemoStoreResponse> call, Throwable t) {
                Toast.makeText(AddMemoActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.pradiptaagus.app_project4.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.GetMemoByIdResponse;
import com.example.pradiptaagus.app_project4.Model.UpdateMemoResponse;
import com.example.pradiptaagus.app_project4.R;

import retrofit2.Callback;
import retrofit2.Response;

public class UpdateMemoActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etMemoTitle;
    private EditText etMemoDetail;
    private String title;
    private String detail;
    private Button btnSave;
    private Button btnDiscard;
    private String token;
    private int userId;
    private int memoId;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_memo_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        // handling toolbar menu on click
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateMemoActivity.this, MainActivity.class));
            }
        });

        // declare share preference
        final SharedPreferences userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);

        // get token from shared preference
        token = userPreference.getString("token", "missing");
        userId = userPreference.getInt("userId", 0);

        //get memo id from previous activity
        Intent intent = getIntent();
        memoId = intent.getIntExtra("memo_id", 0);

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

        getMemoData(token, memoId);
    }

    private void getMemoData(String token, int memoId) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        retrofit2.Call<GetMemoByIdResponse> call = apiInterface.getMemoById(memoId, token);
        call.enqueue(new Callback<GetMemoByIdResponse>() {
            @Override
            public void onResponse(retrofit2.Call<GetMemoByIdResponse> call, Response<GetMemoByIdResponse> response) {
                etMemoTitle.setText(response.body().getTitle());
                etMemoDetail.setText(response.body().getDetail());
            }

            @Override
            public void onFailure(retrofit2.Call<GetMemoByIdResponse> call, Throwable t) {
                Toast.makeText(UpdateMemoActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                updateMemo(token, memoId, userId);
                break;
            case R.id.btn_discard:
                startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void updateMemo(String token, int memoId, int userId) {
        // get user input
        title = etMemoTitle.getText().toString();
        detail = etMemoDetail.getText().toString();

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        retrofit2.Call<UpdateMemoResponse> call = apiInterface.updateMemo(memoId, token, title, detail, userId);
        call.enqueue(new Callback<UpdateMemoResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UpdateMemoResponse> call, Response<UpdateMemoResponse> response) {
                Toast.makeText(UpdateMemoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateMemoActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(retrofit2.Call<UpdateMemoResponse> call, Throwable t) {
                Toast.makeText(UpdateMemoActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.pradiptaagus.app_project4.Activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.StoreMemoResponse;
import com.example.pradiptaagus.app_project4.R;

import retrofit2.Callback;
import retrofit2.Response;

public class AddMemoActivity extends AppCompatActivity {
    private EditText etMemoTitle;
    private EditText etMemoDetail;
    private String title;
    private String detail;
    private String token;
    private int userId;
    private ApiInterface apiInterface;
    SharedPreferences userPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (token == "missing") {
            loginActivity();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Memo");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        // handling toolbar menu on click
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // declare share preference
        userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);

        // get token from shared preference
        token = userPreference.getString("token", "missing");
        userId = userPreference.getInt("userId", 0);

        // get user input
        etMemoTitle = findViewById(R.id.et_memo_title);
        etMemoDetail = findViewById(R.id.et_memo_detail);
    }

    private void loginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }


    private void storeMemo(String token) {
        // get user input
        title = etMemoTitle.getText().toString();
        detail = etMemoDetail.getText().toString();

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        retrofit2.Call<StoreMemoResponse> call = apiInterface.storeMemo(token, title, detail, userId);
        call.enqueue(new Callback<StoreMemoResponse>() {
            @Override
            public void onResponse(retrofit2.Call<StoreMemoResponse> call, Response<StoreMemoResponse> response) {
                Toast.makeText(AddMemoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                FragmentTransaction homeFragment = getFragmentManager().beginTransaction();

            }

            @Override
            public void onFailure(retrofit2.Call<StoreMemoResponse> call, Throwable t) {
                Toast.makeText(AddMemoActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_memo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                storeMemo(token);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

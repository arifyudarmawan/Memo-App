package com.example.pradiptaagus.app_project4.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Fragment.HomeFragment;
import com.example.pradiptaagus.app_project4.Model.LoginResponse;
import com.example.pradiptaagus.app_project4.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private TextView tvSignup;
    private EditText etEmail;
    private EditText etPassword;
    private ApiInterface apiInterface;
    private ProgressBar progressBar;
    private SharedPreferences userPreference;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get EditText element
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        tvSignup = findViewById(R.id.tv_go_to_signup_view);
        tvSignup.setOnClickListener(this);

        // get element progressbar
        progressBar = findViewById(R.id.pb_login);
        // set progressbar invisible
        progressBar.setVisibility(View.INVISIBLE);

        // get token from shared preference
        userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        String token = userPreference.getString("token", "missing");

        if (token != "missing") {
            loginActivity();
        }
    }

    private void loginActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;

            case R.id.tv_go_to_signup_view:
                signup();
                break;
        }
    }

    private void signup() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void login() {
        progressBar.setVisibility(View.VISIBLE);
        //Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                //Toast.makeText(getApplicationContext(), ""+response.body().isStatus(),Toast.LENGTH_SHORT).show();
                if (response.body().isStatus()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), response.body().getToken(), Toast.LENGTH_SHORT).show();
                    // save token to shared preference
                    SharedPreferences.Editor editor = userPreference.edit();
                    editor.putString("token", response.body().getToken());
                    editor.apply();

                    // go to home activity
                    loginActivity();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.pradiptaagus.app_project4.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
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
    private ProgressDialog progressDialog;

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

        // ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");


        // get token from shared preference
        userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        String token = userPreference.getString("token", "missing");

        // check token
        if (token != "missing") {
            loginActivity();
        }
    }

    // go to login activity method
    private void loginActivity() {
        startActivity(new Intent(this, MainActivity.class));
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

    // go to sign up method
    private void signup() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void login() {
        progressDialog.show();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().isStatus()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    // save token to shared preference
                    SharedPreferences.Editor editor = userPreference.edit();
                    editor.putString("token", response.body().getToken());
                    editor.apply();

                    // go to home activity
                    loginActivity();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

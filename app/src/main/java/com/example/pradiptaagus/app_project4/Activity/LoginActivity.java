package com.example.pradiptaagus.app_project4.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiService;
import com.example.pradiptaagus.app_project4.Model.LoginItemResponse;
import com.example.pradiptaagus.app_project4.Model.LoginResponse;
import com.example.pradiptaagus.app_project4.Model.StoreFcmTokenResponse;
import com.example.pradiptaagus.app_project4.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private TextView tvSignup;
    private EditText etEmail;
    private EditText etPassword;
    private ProgressDialog progressDialog;
    private List<LoginItemResponse> loginList = new ArrayList<>();
    private SharedPreferences userPreference, firebasePreference;
    private String token, fcmToken;

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
        firebasePreference = this.getSharedPreferences("fire_base", Context.MODE_PRIVATE);
        fcmToken = firebasePreference.getString("fcm_token", "missing");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;

            case R.id.tv_go_to_signup_view:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }

    private void login() {
        progressDialog.show();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        ApiService apiInterface = ApiClient.getApiClient().create(ApiService.class);
        Call<LoginResponse> call = apiInterface.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().isStatus()) {

                    loginList.addAll(response.body().getAccount());
                    int id = loginList.get(0).getId();
                    String username = loginList.get(0).getName();
                    String email = loginList.get(0).getEmail();
                    String token = response.body().getToken();

                    // store fcm token
                    storeFcmToken(id, token, fcmToken);

                    // save token to shared preference
                    SharedPreferences.Editor editor = userPreference.edit();
                    editor.putString("token", token);
                    editor.putInt("userId", id);
                    editor.putString("userName", username);
                    editor.putString("userEmail", email);
                    editor.apply();

                    progressDialog.dismiss();

                    // go to home activity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

    private void storeFcmToken(int userId, String token, String fcmToken) {
        ApiService apiService = ApiClient.getApiClient().create(ApiService.class);
        Call<StoreFcmTokenResponse> call = apiService.storeFcmToken(userId, token, fcmToken);
        call.enqueue(new Callback<StoreFcmTokenResponse>() {
            @Override
            public void onResponse(Call<StoreFcmTokenResponse> call, Response<StoreFcmTokenResponse> response) {
                Log.d("RESPONSE ", "" + response.body());
            }

            @Override
            public void onFailure(Call<StoreFcmTokenResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

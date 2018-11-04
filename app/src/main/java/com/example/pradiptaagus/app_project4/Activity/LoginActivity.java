package com.example.pradiptaagus.app_project4.Activity;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.LoginResponse;
import com.example.pradiptaagus.app_project4.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private TextView signup;
    private EditText etEmail;
    private EditText etPassword;
    private ApiInterface apiInterface;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb_login);
        progressBar.setVisibility(View.INVISIBLE);
        final SharedPreferences userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        // get token from shared preference
        String token = userPreference.getString("token", "missing");


        if (token != "missing") {
            homeActivity();
        }

        // login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                //Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                // call login method
                login(email, password);
            }

            private void login(String email, String password) {
                ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                Call<LoginResponse> call = apiInterface.login(email, password);

                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        //Toast.makeText(getApplicationContext(), ""+response.body().isStatus(),Toast.LENGTH_SHORT).show();
                        if (response.body().isStatus()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            // save token to shared preference
                            SharedPreferences.Editor editor = userPreference.edit();
                            editor.putString("token", response.body().getToken());
                            editor.apply();

                            // go to home activity
                            homeActivity();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        // go to sign up view
        signup = (TextView) findViewById(R.id.tv_go_to_signup_view);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void homeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}

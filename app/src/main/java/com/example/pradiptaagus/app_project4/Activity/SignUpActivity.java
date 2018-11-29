package com.example.pradiptaagus.app_project4.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.SignUpResponse;
import com.example.pradiptaagus.app_project4.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private TextView tv_login;
    private EditText et_name;
    private EditText et_email;
    private EditText et_password;
    private EditText et_confirm_password;
    private ProgressDialog progresDialog;
    private Button btn_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_name = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        tv_login = findViewById(R.id.tv_go_to_login_view);
        btn_sign_up = findViewById(R.id.btn_sign_up);

        // ProgressDialog
        progresDialog = new ProgressDialog(this);
        progresDialog.setMessage("Creating new account...");

        // go to login view
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //sign up
        btn_sign_up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                String confirm_password = et_confirm_password.getText().toString();

                if (confirm_password.equals(password)) {
                    progresDialog.show();

                    // call signup method
                    signUp(name, email, password);

                } else if (confirm_password != password){
                    Toast.makeText(getApplicationContext(), "Confirm password doesn't match to password", Toast.LENGTH_SHORT).show();
                }
            }

            private void signUp(String name, String email, String password) {
                ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                Call<SignUpResponse> call = apiInterface.signup(name, email, password);

                call.enqueue(new Callback<SignUpResponse>() {
                    @Override
                    public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                        if (response.body().isStatus()) {
                            progresDialog.dismiss();
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            progresDialog.dismiss();
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SignUpResponse> call, Throwable t) {
                        progresDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "1Connection error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        // go to login view
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }

}

package com.example.pradiptaagus.app_project4.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Activity.LoginActivity;
import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.LogoutResponse;
import com.example.pradiptaagus.app_project4.Model.UserResponse;
import com.example.pradiptaagus.app_project4.R;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilFragment extends Fragment implements View.OnClickListener{
    private SharedPreferences userPreference;
    private String name;
    private String email;
    private int id;
    private String token;
    private TextView tvName;
    private TextView tvEmail;
    private Button btnLogout;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    public static ProfilFragment newInstance() {
        return new ProfilFragment();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_profil, container, false);

        // get shared preference
        userPreference = view.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        id = userPreference.getInt("userId", 0);
        name = userPreference.getString("userName", "missing");
        email = userPreference.getString("userEmail", "missing");
        token = userPreference.getString("token", "missing");

        //get element
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

        //progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loging out...");

        // set data to element
        if (id != 0 || name != "missing" || email != "missing") {
            tvName.setText(name);
            tvEmail.setText(email);
        }

        // retrieve data from api
        getUserData(token);

        return view;
    }

    private void getUserData(String token) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<UserResponse> call = apiInterface.getUser(token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                id = response.body().getId();
                name = response.body().getName();
                email = response.body().getEmail();
                tvName.setText(name);
                tvEmail.setText(email);

                //store user data to shared preference
                SharedPreferences.Editor editor = userPreference.edit();
                editor.putInt("userId", id);
                editor.putString("userName", name);
                editor.putString("userEmail", email);
                editor.apply();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                progressDialog.show();
                logout(token);
        }
    }

    private void logout(String token) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<LogoutResponse> call = apiInterface.logout(token);
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                progressDialog.dismiss();
                String message = response.body().getMessage();
                SharedPreferences.Editor editor = userPreference.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                //go to login activity
                startActivity(new Intent(getContext(), LoginActivity.class));
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

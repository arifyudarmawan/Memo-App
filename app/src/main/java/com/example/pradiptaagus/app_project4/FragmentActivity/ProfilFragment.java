package com.example.pradiptaagus.app_project4.FragmentActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Activity.LoginActivity;
import com.example.pradiptaagus.app_project4.Activity.SearchFriendActivity;
import com.example.pradiptaagus.app_project4.Adapter.FriendAdapter;
import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.FriendItemResponse;
import com.example.pradiptaagus.app_project4.Model.FriendResponse;
import com.example.pradiptaagus.app_project4.Model.LogoutResponse;
import com.example.pradiptaagus.app_project4.Model.UserResponse;
import com.example.pradiptaagus.app_project4.R;
import com.example.pradiptaagus.app_project4.SQLite.DBHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v7.widget.LinearLayoutManager.*;

public class ProfilFragment extends Fragment implements View.OnClickListener{
    private SharedPreferences userPreference;
    private String name;
    private String email;
    private int id;
    private String token;
    private TextView tvName;
    private TextView tvEmail;
    private ImageView ivLogout;
    private ApiInterface apiInterface;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private List<FriendItemResponse> friendList = new ArrayList<>();
    private FriendAdapter adapter;
    private DBHelper dbHelper;

    public static ProfilFragment newInstance() {
        return new ProfilFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        // get element
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        ivLogout = view.findViewById(R.id.iv_logout);
        ivLogout.setOnClickListener(this);

        // progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loging out...");

        // dbHelper
        dbHelper = new DBHelper(getContext());

        // recyclerView
        recyclerView = view.findViewById(R.id.friend_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // insert data to recyclerview
        init();

        // set data to element
        if (id != 0 || name != "missing" || email != "missing") {
            tvName.setText(name);
            tvEmail.setText(email);
        }

        // retrieve data from api
        if (id == 0 || name == "missing" || email == "missing" || token == "missing") {
            getUserData(token);
        } else {
            loadFromSharedPreference();
        }

        return view;
    }

    private void init() {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<FriendResponse> call = apiInterface.getAllFriend(id, token);
        call.enqueue(new Callback<FriendResponse>() {
            @Override
            public void onResponse(Call<FriendResponse> call, Response<FriendResponse> response) {
                // get all response from api
                friendList.addAll(response.body().getData());
                adapter = new FriendAdapter(friendList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<FriendResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFromDatabase() {

    }

    private void storeToLocalDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < friendList.size(); i++) {
            values.put("id", friendList.get(i).getId());
            values.put("name", friendList.get(i).getName());
            values.put("email", friendList.get(i).getEmail());
        }

        db.insert("friends", null, values);
    }

    private void loadFromSharedPreference() {
        tvName.setText(name);
        tvEmail.setText(email);
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
            case R.id.iv_logout:
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
                getActivity().finish();
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profil_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                startActivity(new Intent(getContext(), SearchFriendActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.pradiptaagus.app_project4.Fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Activity.LoginActivity;
import com.example.pradiptaagus.app_project4.Activity.SearchFriendActivity;
import com.example.pradiptaagus.app_project4.Adapter.FriendAdapter;
import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiService;
import com.example.pradiptaagus.app_project4.Model.FriendItemResponse;
import com.example.pradiptaagus.app_project4.Model.FriendNumberResponse;
import com.example.pradiptaagus.app_project4.Model.FriendResponse;
import com.example.pradiptaagus.app_project4.Model.LogoutResponse;
import com.example.pradiptaagus.app_project4.R;
import com.example.pradiptaagus.app_project4.SQLite.DBHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private SharedPreferences userPreference;
    private String name;
    private String email;
    private int id;
    private String token;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvNumberOfFriend;
    private ImageView ivLogout;
    private ApiService apiInterface;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private List<FriendItemResponse> friendList = new ArrayList<>();
    private FriendAdapter adapter;
    private DBHelper dbHelper;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static ProfilFragment newInstance() {
        return new ProfilFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
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
        progressBar = view.findViewById(R.id.pb_load_friend);
        tvNumberOfFriend = view.findViewById(R.id.tv_number_of_friend);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // set data to element
        if (id != 0 || name != "missing" || email != "missing") {
            tvName.setText(name);
            tvEmail.setText(email);
        }

        // load data
        init(id, token);
    }

    private void init(int id, String token) {
        if (this.isConnected()) {
            setProfil();
            loadFriend(id, token);
            numberOfFriend();
        } else {
            loadFromSharedPreference();
            loadFromLocalDatabase();
        }
    }

    private void setProfil() {
        tvName.setText(name);
        tvEmail.setText(email);
    }

    private void loadFriend(int id, String token) {
        apiInterface = ApiClient.getApiClient().create(ApiService.class);
        Call<FriendResponse> call = apiInterface.getAllFriend(id, token);
        call.enqueue(new Callback<FriendResponse>() {
            @Override
            public void onResponse(Call<FriendResponse> call, Response<FriendResponse> response) {
                if (response.body() != null) {
                    // get all response from api
                    friendList.clear();
                    friendList.addAll(response.body().getData());
                    adapter = new FriendAdapter(friendList);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);

                    deleteAllLocalMemo();
                    storeFriendToLocalDatabase();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load friend list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FriendResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void numberOfFriend() {
        apiInterface = ApiClient.getApiClient().create(ApiService.class);
        Call<FriendNumberResponse> call = apiInterface.numberOfFriend(id, token);
        call.enqueue(new Callback<FriendNumberResponse>() {
            @Override
            public void onResponse(Call<FriendNumberResponse> call, Response<FriendNumberResponse> response) {
                int friend = response.body().getFriend();
                tvNumberOfFriend.setText("(" + Integer.toString(friend) + ")");
            }

            @Override
            public void onFailure(Call<FriendNumberResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFromLocalDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "id",
                "name",
                "email",
        };

        Cursor cursor = db.query(
                "friends",
                projection,
                null,
                null,
                null,
                null,
                "id DESC"
        );

        while (cursor.moveToNext()) {
            FriendItemResponse friend = new FriendItemResponse();
            friend.setId(cursor.getInt(0));
            friend.setName(cursor.getString(1));
            friend.setEmail(cursor.getString(2));

            friendList.add(friend);
        }
        cursor.close();
        adapter = new FriendAdapter(friendList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void deleteAllLocalMemo() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("friends", null, null);
    }

    private void storeFriendToLocalDatabase() {
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
        Toast.makeText(getContext(), "Load from sharedpreference", Toast.LENGTH_SHORT).show();
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean status = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return status;
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
        apiInterface = ApiClient.getApiClient().create(ApiService.class);
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        init(id, token);
        swipeRefreshLayout.setRefreshing(false);
    }
}

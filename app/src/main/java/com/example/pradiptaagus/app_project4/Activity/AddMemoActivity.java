package com.example.pradiptaagus.app_project4.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Adapter.RecipientAdapter;
import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiService;
import com.example.pradiptaagus.app_project4.Model.FriendItemResponse;
import com.example.pradiptaagus.app_project4.Model.FriendResponse;
import com.example.pradiptaagus.app_project4.Model.StoreMemoResponse;
import com.example.pradiptaagus.app_project4.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemoActivity extends AppCompatActivity {
    private EditText etMemoTitle;
    private EditText etMemoDetail;
    private String token;
    private int userId;
    private ApiService apiService;
    public ArrayList<FriendItemResponse> recipientList = new ArrayList<>();
    private List<FriendItemResponse> friendList = new ArrayList<>();
    private RecipientAdapter recipientAdapter;
    private RecyclerView recyclerView;
    private List<Integer> recipient_id;
    private ScrollView scrollView;
    private ProgressBar progressBar;

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
        SharedPreferences userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);

        // get token from shared preference
        token = userPreference.getString("token", "missing");
        userId = userPreference.getInt("userId", 0);

        // recyclerview
        recyclerView = findViewById(R.id.recipient_recycler_view);
//        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // get user input
        etMemoTitle = findViewById(R.id.et_memo_title);
        etMemoDetail = findViewById(R.id.et_memo_detail);
        scrollView = findViewById(R.id.scroll_view);
        progressBar = findViewById(R.id.pb_load_add_memo);

        init();


    }

    private void init() {
        if (this.isConnected()) {
            loadFromServer();
        } else {
            loadFromLocalDatabase();
        }
    }

    private void loadFromLocalDatabase() {

    }

    private void loginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void loadFromServer() {
        apiService = ApiClient.getApiClient().create(ApiService.class);
        Call<FriendResponse> call = apiService.getAllFriend(userId, token);
        call.enqueue(new Callback<FriendResponse>() {
            @Override
            public void onResponse(Call<FriendResponse> call, Response<FriendResponse> response) {
                if (response.body() != null) {
                    // get all response from api
                    friendList.clear();
                    friendList.addAll(response.body().getData());
                    recipientAdapter = new RecipientAdapter(friendList);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(recipientAdapter);
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to load friend list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FriendResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean status = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return status;
    }


    private void storeMemo(String token) {
        // get user input
        String title = etMemoTitle.getText().toString();
        String detail = etMemoDetail.getText().toString();

        // get recipient
        recipient_id = new ArrayList<>();

        for (FriendItemResponse recipient : recipientAdapter.checkedFriendList) {
            recipient_id.add(recipient.getId());
        }

        if (recipientAdapter.checkedFriendList.size() > 0) {
            Toast.makeText(this, "" + recipient_id, Toast.LENGTH_SHORT).show();
            storeMemoWithRecipient(title, detail, userId, token, recipient_id);
        } else {
            Toast.makeText(this, "No recipient selected.", Toast.LENGTH_SHORT).show();
            storeMemoWithoutRecipient(title, detail, userId, token);
        }
    }

    private void storeMemoWithRecipient(String title, String detail, int userId, String token, List<Integer> recipient_id) {
        apiService = ApiClient.getApiClient().create(ApiService.class);
        Call<StoreMemoResponse> call = apiService.storeMemoWithRecipient(token, title,detail, userId, recipient_id);
        call.enqueue(new Callback<StoreMemoResponse>() {
            @Override
            public void onResponse(Call<StoreMemoResponse> call, Response<StoreMemoResponse> response) {
                Toast.makeText(AddMemoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<StoreMemoResponse> call, Throwable t) {
                Toast.makeText(AddMemoActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeMemoWithoutRecipient(String title, String detail, int userId, String token) {
        apiService = ApiClient.getApiClient().create(ApiService.class);
        Call<StoreMemoResponse> call = apiService.storeMemo(token, title, detail, userId);
        call.enqueue(new Callback<StoreMemoResponse>() {
            @Override
            public void onResponse(Call<StoreMemoResponse> call, Response<StoreMemoResponse> response) {
                Toast.makeText(AddMemoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                finish();
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

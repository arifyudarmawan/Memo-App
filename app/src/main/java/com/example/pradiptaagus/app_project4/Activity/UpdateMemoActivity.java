package com.example.pradiptaagus.app_project4.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiService;
import com.example.pradiptaagus.app_project4.Model.ShowMemoResponse;
import com.example.pradiptaagus.app_project4.Model.UpdateMemoResponse;
import com.example.pradiptaagus.app_project4.R;
import com.example.pradiptaagus.app_project4.SQLite.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Callback;
import retrofit2.Response;

public class UpdateMemoActivity extends AppCompatActivity {
    private EditText etMemoTitle;
    private EditText etMemoDetail;
    private String title;
    private String detail;
    private String token;
    private int userId;
    private int memoId;
    private ApiService apiService;
    private DBHelper dbHelper;
    private SharedPreferences userPreference;
    private ProgressBar progressBar;
    private TextInputLayout layoutMemoTitle, layoutMemoDetail;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (token == "missing") {
            loginActivity();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_memo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Memo");
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

        dbHelper = new DBHelper(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");

        //get memo id from previous activity
        Intent intent = getIntent();
        memoId = intent.getIntExtra("memo_id", 0);

        // get user input
        etMemoTitle = findViewById(R.id.et_memo_title);
        etMemoDetail = findViewById(R.id.et_memo_detail);
        progressBar = findViewById(R.id.pb_load_memo);
        layoutMemoTitle = findViewById(R.id.layout_memo_title);
        layoutMemoDetail = findViewById(R.id.layout_memo_detail);

        getMemoData(token, memoId);
    }

    private void getMemoData(String token, int memoId) {
        apiService = ApiClient.getApiClient().create(ApiService.class);
        retrofit2.Call<ShowMemoResponse> call = apiService.showMemo(memoId, token);
        call.enqueue(new Callback<ShowMemoResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ShowMemoResponse> call, Response<ShowMemoResponse> response) {
                etMemoTitle.setText(response.body().getMemo().getTitle());
                etMemoDetail.setText(response.body().getMemo().getDetail());
                progressBar.setVisibility(View.GONE);

                layoutMemoTitle.setVisibility(View.VISIBLE);
                layoutMemoDetail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(retrofit2.Call<ShowMemoResponse> call, Throwable t) {
                Toast.makeText(UpdateMemoActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void updateMemo(String token, final int memoId, int userId) {
        progressDialog.show();

        // get user input
        title = etMemoTitle.getText().toString();
        detail = etMemoDetail.getText().toString();

        apiService = ApiClient.getApiClient().create(ApiService.class);
        retrofit2.Call<UpdateMemoResponse> call = apiService.updateMemo(memoId, token, title, detail, userId);
        call.enqueue(new Callback<UpdateMemoResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UpdateMemoResponse> call, Response<UpdateMemoResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateMemoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    // update sqlite
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();

                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    //store data to Contentvalues variable
                    contentValues.put("title", title);
                    contentValues.put("detail", detail);
                    contentValues.put("updated_at", timestamp);

                    String args[] = new String[]{Integer.toString(memoId)};

                    db.update("memos", contentValues, "id=?", args);

                    finish();
                } else {
                    Toast.makeText(UpdateMemoActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(retrofit2.Call<UpdateMemoResponse> call, Throwable t) {
                Toast.makeText(UpdateMemoActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
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
                updateMemo(token, memoId, userId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

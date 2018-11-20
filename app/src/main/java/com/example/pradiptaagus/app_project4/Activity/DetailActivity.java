package com.example.pradiptaagus.app_project4.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.GetMemoByIdResponse;
import com.example.pradiptaagus.app_project4.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Inflater;

import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private TextView tvTitle;
    private TextView tvDetail;
    private TextView tvDate;
    private String title;
    private String detail;
    private String token;
    private String date;
    private int userId;
    private int memoId;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        SharedPreferences userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        token = userPreference.getString("token", "missing");
        userId = userPreference.getInt("memoId", 0);

        //get memo_id from previous activity
        Intent intent = getIntent();
        memoId = intent.getIntExtra("memo_id", 0);

        tvTitle = findViewById(R.id.tv_memo_title);
        tvDetail = findViewById(R.id.tv_memo_detail);
        tvDate = findViewById(R.id.tv_memo_date);

        // scrolling textview tv_memo_detail
        tvDetail.setMovementMethod(new ScrollingMovementMethod());

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        // handling toolbar menu on click
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this, MainActivity.class));
                finish();
            }
        });

        if (token == "missing") {
            startActivity(new Intent(this, LoginActivity.class));
        }

        getMemoData(memoId, token);
    }

    private void getMemoData(int memoId, String token) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        retrofit2.Call<GetMemoByIdResponse> call = apiInterface.getMemoById(memoId, token);
        call.enqueue(new Callback<GetMemoByIdResponse>() {
            @Override
            public void onResponse(retrofit2.Call<GetMemoByIdResponse> call, Response<GetMemoByIdResponse> response) {
                title = response.body().getTitle();
                detail = response.body().getDetail();
                date = (String) response.body().getCreatedAt();

                tvTitle.setText(title);
                tvDetail.setText(detail);
                tvDate.setText(formatDate(date));

            }

            private String formatDate(String updateAt) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = simpleDateFormat.parse(updateAt);
                    SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat("dd-MMM-yyyy");
                    return simpleDateFormatOut.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            public void onFailure(retrofit2.Call<GetMemoByIdResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_memo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(this, UpdateMemoActivity.class);
                intent.putExtra("memo_id", memoId);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}

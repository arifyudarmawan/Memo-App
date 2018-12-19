package com.example.pradiptaagus.app_project4.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Adapter.FriendAdapter;
import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiService;
import com.example.pradiptaagus.app_project4.Model.DeleteMemoResponse;
import com.example.pradiptaagus.app_project4.Model.FriendItemResponse;
import com.example.pradiptaagus.app_project4.Model.ShowMemoResponse;
import com.example.pradiptaagus.app_project4.R;
import com.example.pradiptaagus.app_project4.SQLite.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvMemoDetail, tvRecipentTitle, tvDetailTitle;
    private TextView tvDetail;
    private TextView tvDate;
    private String token;
    private String date;
    private int userId;
    private int memoId;
    private ApiService apiInterface;
    DBHelper dbHelper;
    private ProgressBar progressBar;
    private List<FriendItemResponse> recipientList = new ArrayList<>();
    private FriendAdapter recipientAdapter;
    private RecyclerView recyclerView;
    private FriendItemResponse recipient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);

        SharedPreferences userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        token = userPreference.getString("token", "missing");
        userId = userPreference.getInt("memoId", 0);

        if (token == "missing") {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        //get memo_id from previous activity
        Intent intent = getIntent();
        memoId = intent.getIntExtra("memo_id", 0);

        tvTitle = findViewById(R.id.tv_memo_title);
        tvDetail = findViewById(R.id.tv_memo_detail);
        tvDate = findViewById(R.id.tv_memo_date);
        progressBar = findViewById(R.id.pb_load_memo);
        tvMemoDetail = findViewById(R.id.tv_memo_detail);
        tvRecipentTitle = findViewById(R.id.tv_recipient_title);
        tvDetailTitle = findViewById(R.id.tv_memo_title_detail);

        dbHelper = new DBHelper(this);

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
                finish();
            }
        });

        // recyclerview
        recyclerView = findViewById(R.id.recipient_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());



        init();
    }

    private void init() {
        if (isConnected()) {
            loadFromServer(memoId);
        } else {
            loadFromDatabase(memoId);
        }
    }

    private void loadFromServer(int memoId) {
        ApiService apiInterface = ApiClient.getApiClient().create(ApiService.class);
        retrofit2.Call<ShowMemoResponse> call = apiInterface.showMemo(memoId, token);
        call.enqueue(new Callback<ShowMemoResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ShowMemoResponse> call, Response<ShowMemoResponse> response) {
                tvTitle.setText(response.body().getMemo().getTitle());
                tvDetail.setText(response.body().getMemo().getDetail());
                tvDate.setText(response.body().getMemo().getUpdatedAt());

                // add item to recyclerview

                for (int i = 0; i < response.body().getRecipient().size(); i++) {
                    recipient = new FriendItemResponse();
                    recipient.setId(response.body().getRecipient().get(i).getId());
                    recipient.setName(response.body().getRecipient().get(i).getName());
                    recipient.setEmail(response.body().getRecipient().get(i).getEmail());

                    recipientList.add(recipient);
                    Log.d("TAG_TEST", "" + recipientList);
                }

                Log.d("ALL_TAG", "" + recipientList);

                recipientAdapter = new FriendAdapter(recipientList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(recipientAdapter);

                tvMemoDetail.setVisibility(View.VISIBLE);
                tvRecipentTitle.setVisibility(View.VISIBLE);
                tvDetailTitle.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(retrofit2.Call<ShowMemoResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load memo!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadFromDatabase(int memoId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "id",
                "title",
                "detail",
                "user_id",
                "created_at",
                "updated_at"
        };

        //filter result where id = memoId
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(memoId)};

        Cursor cursor = db.query(
                "memos",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        tvTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
        tvDetail.setText(cursor.getString(cursor.getColumnIndex("detail")));
        tvDate.setText(formatDate(cursor.getString(cursor.getColumnIndex("updated_at"))));
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean status = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return status;
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
                finish();
                return true;

//            case R.id.delete:
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//                alertDialog.setMessage("Are you sure want to delete this memo?");
//                alertDialog.setCancelable(true);
//                alertDialog.setNegativeButton(
//                        "no",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do nothing
//                            }
//                        }
//                );
//                alertDialog.setPositiveButton(
//                        "yes",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                deleteMemo(memoId);
//                            }
//                        }
//                );
//                alertDialog.create().show();
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteMemo(final int memoId) {
        apiInterface = ApiClient.getApiClient().create(ApiService.class);
        retrofit2.Call<DeleteMemoResponse> call = apiInterface.deleteMemo(memoId, token);
        call.enqueue(new Callback<DeleteMemoResponse>() {
            @Override
            public void onResponse(retrofit2.Call<DeleteMemoResponse> call, Response<DeleteMemoResponse> response) {
                if (response.body().isStatus()) {
                    Toast.makeText(DetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.d("Tag", "error");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<DeleteMemoResponse> call, Throwable t) {
                Log.d("Tag", "error", t);
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //remove item on SQLite
    private void deleteLocalMemo(int memo_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(memo_id)};
        int result = db.delete("memos", selection, selectionArgs);
        Toast.makeText(this, ""+result, Toast.LENGTH_SHORT).show();
    }
}

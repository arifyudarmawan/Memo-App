package com.example.pradiptaagus.app_project4.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.DeleteMemoResponse;
import com.example.pradiptaagus.app_project4.Model.GetMemoByIdResponse;
import com.example.pradiptaagus.app_project4.Model.MemoItemResponse;
import com.example.pradiptaagus.app_project4.R;
import com.example.pradiptaagus.app_project4.SQLite.DBHelper;

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
    DBHelper dbHelper;

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

        if (token == "missing") {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        loadFromDatabase(memoId);
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

            case R.id.delete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("Are you sure want to delete this memo?");
                alertDialog.setCancelable(true);
                alertDialog.setNegativeButton(
                        "no",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }
                );
                alertDialog.setPositiveButton(
                        "yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                deleteMemo(memoId);
                            }
                        }
                );
                alertDialog.create().show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteMemo(final int memoId) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
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

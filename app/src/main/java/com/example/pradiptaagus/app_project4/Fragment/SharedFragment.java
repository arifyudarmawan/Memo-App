package com.example.pradiptaagus.app_project4.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Activity.DetailActivity;
import com.example.pradiptaagus.app_project4.Adapter.MemoAdapter;
import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiService;
import com.example.pradiptaagus.app_project4.Model.MemoItemResponse;
import com.example.pradiptaagus.app_project4.Model.MemoResponse;
import com.example.pradiptaagus.app_project4.R;
import com.example.pradiptaagus.app_project4.SQLite.DBHelper;
import com.example.pradiptaagus.app_project4.Util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SharedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private List<MemoItemResponse> sharedMemoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MemoAdapter adapter;
    private MemoItemResponse memo;
    private ApiService apiService;
    private String token;
    private int userId;
    private DBHelper dbHelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private SharedPreferences userPreference;
    
    
    public static SharedFragment newInstance() {
        return new SharedFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_shared_memo, container, false);

        dbHelper = new DBHelper(getContext());

        // recycler view
        recyclerView = view.findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // shared preference
        userPreference = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = userPreference.getInt("userId", 0);
        token = userPreference.getString("token", "missing");

        //swipe refresh layout
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        //progressbar
        progressBar = view.findViewById(R.id.pb_load_memo);

        swipeRefreshLayout.setOnRefreshListener(this);
        
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                init();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                memo = sharedMemoList.get(position);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("memo_id", memo.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, final int position) {
                memo = sharedMemoList.get(position);
                String[] menu = {"Detail", "Edit", "Delete"};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(getContext(), DetailActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            intent.putExtra("memo_id", memo.getId());
                            startActivity(intent);
                        }
                    }
                });
                alertDialog.create().show();
            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.GONE);
        init();
    }

    private void init() {
        if (this.isConnected()) {
            loadFromServer();
        } else {
            loadFromDatabase();
        }
    }

    private void loadFromServer() {
        apiService = ApiClient.getApiClient().create(ApiService.class);
        Call<MemoResponse> call = apiService.getSharedMemo(userId, token);
        call.enqueue(new Callback<MemoResponse>() {
            @Override
            public void onResponse(Call<MemoResponse> call, Response<MemoResponse> response) {
                if (response.body().isStatus()) {
                    sharedMemoList.clear();
                    sharedMemoList.addAll(response.body().getData());
                    adapter = new MemoAdapter(sharedMemoList);
                    recyclerView.setAdapter(adapter);
                }
                progressBar.setVisibility(View.GONE);

                deleteAllLocalMemo();
                storeToLocalDatabase();
            }

            @Override
            public void onFailure(Call<MemoResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load memo!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAllLocalMemo() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("memos", null, null);
    }

    private void storeToLocalDatabase() {
        // put data into database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //create a new map of values, where column name are the keys
        ContentValues values = new ContentValues();
        for (int i = 0; i < sharedMemoList.size(); i++) {
            values.put("id", sharedMemoList.get(i).getId());
            values.put("title", sharedMemoList.get(i).getTitle());
            values.put("detail", sharedMemoList.get(i).getDetail());
            values.put("user_id", sharedMemoList.get(i).getUserId());
            values.put("created_at", sharedMemoList.get(i).getCreatedAt());
            values.put("updated_at", sharedMemoList.get(i).getUpdatedAt());

            db.insert("memos", null, values);
        }
    }

    private void loadFromDatabase() {
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

        Cursor cursor = db.query(
                "memos",
                projection,
                null,
                null,
                null,
                null,
                "id DESC"
        );

        while (cursor.moveToNext()) {
            MemoItemResponse memo = new MemoItemResponse();
            memo.setId(cursor.getInt(0));
            memo.setTitle(cursor.getString(1));
            memo.setDetail(cursor.getString(2));
            memo.setUserId(cursor.getInt(3));
            memo.setCreatedAt(cursor.getString(4));
            memo.setUpdatedAt(cursor.getString(5));

            sharedMemoList.add(memo);
        }
        cursor.close();
        adapter = new MemoAdapter(sharedMemoList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        init();
        swipeRefreshLayout.setRefreshing(false);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean status = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return status;
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        init();
        swipeRefreshLayout.setRefreshing(false);
    }
}

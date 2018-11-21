package com.example.pradiptaagus.app_project4.FragmentActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Activity.AddMemoActivity;
import com.example.pradiptaagus.app_project4.Activity.DetailActivity;
import com.example.pradiptaagus.app_project4.Activity.UpdateMemoActivity;
import com.example.pradiptaagus.app_project4.Adapter.MemoAdapter;
import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.DeleteMemoResponse;
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

public class HomeFragment extends Fragment {
    private List<MemoItemResponse> memoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MemoAdapter adapter;
    private MemoItemResponse memo;
    private ApiInterface apiInterface;
    private String token;
    DBHelper dbHelper;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_home, container, false);

        SharedPreferences userPreference = view.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        token = userPreference.getString("token", "missing");
        dbHelper = new DBHelper(getContext());

        // recycler view
        recyclerView = view.findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //insert data to recyclerview

        if (this.isConnected()) {
//            Toast.makeText(getContext(), "connect", Toast.LENGTH_SHORT).show();
            prepareMemoData(token);
        } else {
//            Toast.makeText(getContext(), "disconnect", Toast.LENGTH_SHORT).show();
            loadFromDatabase();
        }

//        prepareMemoData(token);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                memo = memoList.get(position);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("memo_id", memo.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, final int position) {
                memo = memoList.get(position);
                String[] menu = {"Detail", "Edit", "Delete"};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(getContext(), DetailActivity.class);
                            intent.putExtra("memo_id", memo.getId());
                            startActivity(intent);
                        } else if (which == 1) {
                            Intent intent = new Intent(getContext(), UpdateMemoActivity.class);
                            intent.putExtra("memo_id", memo.getId());
                            startActivity(intent);
                        } else if (which == 2) {
                            removeRecyclerViewItem(position, memo.getId(), token);
                            Toast.makeText(getContext(), "Delete " +memo.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void removeRecyclerViewItem(int position, int memo_id, String token) {
                        memoList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, memoList.size());

                        // remove item on database
                        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                        Call<DeleteMemoResponse> call = apiInterface.deleteMemo(memo_id, token);
                        call.enqueue(new Callback<DeleteMemoResponse>() {
                            @Override
                            public void onResponse(Call<DeleteMemoResponse> call, Response<DeleteMemoResponse> response) {
                                if (response.body().isStatus()) {
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("Tag", "error");
                                }
                            }

                            @Override
                            public void onFailure(Call<DeleteMemoResponse> call, Throwable t) {
                                Log.d("Tag", "error", t);
                                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                alertDialog.create().show();
            }
        }));
        return view;
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean status = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return status;
    }

    private void prepareMemoData(String token) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<MemoResponse> call = apiInterface.getAllMemo(token);
        call.enqueue(new Callback<MemoResponse>() {
            @Override
            public void onResponse(Call<MemoResponse> call, Response<MemoResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().isStatus()) {
                        // get all response from api
                        memoList.addAll(response.body().getData());

                        // store to local database (SQLite)
                        storeToLocalDatabase();

                        // clear memoList cache
                        memoList.clear();

                        // load data from local database (SQLite)
                        loadFromDatabase();
                    } else {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Internal server error", Toast.LENGTH_SHORT).show();
                }

            }

            private void storeToLocalDatabase() {
                // put data into database
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //create a new map of values, where column name are the keys
                ContentValues values = new ContentValues();
                for (int i = 0; i < memoList.size(); i++) {
                    values.put("id", memoList.get(i).getId());
                    values.put("title", memoList.get(i).getTitle());
                    values.put("detail", memoList.get(i).getDetail());
                    values.put("user_id", memoList.get(i).getUserId());
                    values.put("created_at", memoList.get(i).getCreatedAt());
                    values.put("updated_at", memoList.get(i).getUpdatedAt());

                    db.insert("memos", null, values);
                }
            }

            @Override
            public void onFailure(Call<MemoResponse> call, Throwable t) {
//                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
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

            memoList.add(memo);
        }
        cursor.close();
        adapter = new MemoAdapter(memoList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_memo:
                startActivity(new Intent(getContext(), AddMemoActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package com.example.pradiptaagus.app_project4.FragmentActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Activity.AddMemoActivity;
import com.example.pradiptaagus.app_project4.Adapter.MemoAdapter;
import com.example.pradiptaagus.app_project4.Api.ApiClient;
import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Model.MemoItem;
import com.example.pradiptaagus.app_project4.Model.MemoResponse;
import com.example.pradiptaagus.app_project4.R;
import com.example.pradiptaagus.app_project4.Util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private List<MemoItem> memoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MemoAdapter adapter;
    private FloatingActionButton fab;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_home, container, false);

        SharedPreferences userPreference = view.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String token = userPreference.getString("token", "missing");

        // floating button action
        fab = view.findViewById(R.id.fab_add_memo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddMemoActivity.class);
                startActivity(intent);
            }
        });

        // recycler view
        recyclerView = view.findViewById(R.id.my_recycler_view);
        adapter = new MemoAdapter(memoList);
        recyclerView.setHasFixedSize(true);
        // vertical recycler view
        // keep item_recycler_view width to match_parent
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // adding divider line
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MemoItem memo = memoList.get(position);
                memo.getId();
                Toast.makeText(getContext(), memo.getTitle() + " is selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                String[] menu = {"Detail", "Edit", "Delete"};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Menu");
                alertDialog.setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Toast.makeText(getContext(), "Detail", Toast.LENGTH_SHORT).show();
                        } else if (which == 1) {
                            Toast.makeText(getContext(), "Edit", Toast.LENGTH_SHORT).show();
                        } else if (which == 2) {
                            Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.create().show();

                MemoItem memo = memoList.get(position);
                Toast.makeText(getContext(), memo.getDetail(), Toast.LENGTH_SHORT).show();
            }
        }));

        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        prepareMemoData(token);

        return view;
    }

    private void prepareMemoData(String token) {
        Toast.makeText(getContext(), token, Toast.LENGTH_SHORT).show();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<MemoResponse> call = apiInterface.getAllMemo(token);
        call.enqueue(new Callback<MemoResponse>() {
            @Override
            public void onResponse(Call<MemoResponse> call, Response<MemoResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().isStatus()) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        memoList.clear();
                        memoList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Internal server error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<MemoResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}

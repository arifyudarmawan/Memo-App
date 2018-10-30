package com.example.pradiptaagus.app_project4.Activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Adapter.MemoAdapter;
import com.example.pradiptaagus.app_project4.Model.Memo;
import com.example.pradiptaagus.app_project4.R;
import com.example.pradiptaagus.app_project4.Util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private List<Memo> memoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MemoAdapter adapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // floating button action
        fab = (FloatingActionButton) findViewById(R.id.fab_add_memo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            }
        });

        // recycler view
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        adapter = new MemoAdapter(memoList);
        recyclerView.setHasFixedSize(true);
        // vertical recycler view
        // keep item_recycler_view width to match_parent
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // adding divider line
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Memo memo = memoList.get(position);
                Toast.makeText(getApplicationContext(), memo.getTitle() + " is selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Memo memo = memoList.get(position);
                Toast.makeText(getApplicationContext(), memo.getDetail(), Toast.LENGTH_SHORT).show();
            }
        }));

        recyclerView.setAdapter(adapter);

        prepareMemoData();
    }

    private void prepareMemoData() {

        Memo memo = new Memo("Memo", "Description memo");
        memoList.add(memo);

        Memo memo2 = new Memo("Memo", "Description memo");
        memoList.add(memo2);

        Memo memo3 = new Memo("Memo", "Description memo");
        memoList.add(memo3);

        Memo memo4 = new Memo("Memo", "Description memo");
        memoList.add(memo4);

        Memo memo5 = new Memo("Memo", "Description memo");
        memoList.add(memo5);

        Memo memo6 = new Memo("Memo", "Description memo");
        memoList.add(memo6);

        Memo memo7 = new Memo("Memo", "Description memo");
        memoList.add(memo7);

        Memo memo8 = new Memo("Memo", "Description memo");
        memoList.add(memo8);

        Memo memo9 = new Memo("Memo", "Description memo");
        memoList.add(memo9);

        Memo memo10 = new Memo("Memo", "Description memo");
        memoList.add(memo10);
        adapter.notifyDataSetChanged();
    }
}

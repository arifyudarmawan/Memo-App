package com.example.pradiptaagus.app_project4.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pradiptaagus.app_project4.Model.MemoItemResponse;
import com.example.pradiptaagus.app_project4.R;

import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {
    private List<MemoItemResponse> memoList;

    public class MemoViewHolder extends RecyclerView.ViewHolder {
        public TextView title, detail;

        public MemoViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tv_memo_title);
            detail = (TextView) view.findViewById(R.id.tv_memo_detail);
        }
    }

    public MemoAdapter(List<MemoItemResponse> memoList) {
        this.memoList = memoList;
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recycler_view, viewGroup, false);
        return new MemoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder memoViewHolder, int position) {
        MemoItemResponse memo = memoList.get(position);
        memoViewHolder.title.setText(memo.getTitle());
        memoViewHolder.detail.setText(memo.getDetail());
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }




}

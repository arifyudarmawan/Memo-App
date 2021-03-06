package com.example.pradiptaagus.app_project4.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pradiptaagus.app_project4.Model.MemoItemResponse;
import com.example.pradiptaagus.app_project4.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {
    private List<MemoItemResponse> memoList;

    public class MemoViewHolder extends RecyclerView.ViewHolder {
        public TextView title, detail, date, recipient;

        public MemoViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tv_memo_title);
            detail = view.findViewById(R.id.tv_memo_detail);
            date = view.findViewById(R.id.tv_memo_date);
            recipient = view.findViewById(R.id.tv_recipient_number);

        }
    }

    public MemoAdapter(List<MemoItemResponse> memoList) {
        this.memoList = memoList;
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_memo_item_recyclerview, viewGroup, false);
        return new MemoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder memoViewHolder, int position) {
        MemoItemResponse memo = memoList.get(position);
        String title = memo.getTitle();
        if (title.length() >= 22) {
            title = title.substring(0,22).concat("...");
        }

        String detail = memo.getDetail();
        if (detail.length() >= 130) {
            detail = detail.substring(0, 130).concat("...");
        }

        int recipient = memo.getRecipient();

        memoViewHolder.title.setText(title);
        memoViewHolder.detail.setText(detail);

        if (recipient > 0) {
            memoViewHolder.recipient.setText("(" + recipient + ")");
        }

//        memoViewHolder.date.setText(formatDate(memo.getUpdatedAt()));
    }

    private String formatDate(String updatedAt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(updatedAt);
            SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat("dd-MMM-yyyy");
            return simpleDateFormatOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

}

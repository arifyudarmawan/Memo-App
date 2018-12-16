package com.example.pradiptaagus.app_project4.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.pradiptaagus.app_project4.Model.FriendItemResponse;
import com.example.pradiptaagus.app_project4.R;
import com.example.pradiptaagus.app_project4.Util.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class RecipientAdapter extends RecyclerView.Adapter<RecipientAdapter.RecipientViewHolder> {
    private List<FriendItemResponse> friendList;
    public ArrayList<FriendItemResponse> checkedFriendList = new ArrayList<>();

    public class RecipientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView username, email;
        public CheckBox checkBox;
        ItemClickListener itemClickListener;

        public RecipientViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tv_username);
            email = itemView.findViewById(R.id.tv_email);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener item) {
            this.itemClickListener = item;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }
    }

    public RecipientAdapter(List<FriendItemResponse> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public RecipientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_memo_recipient_item_recycler_view, viewGroup, false);
        return new RecipientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipientViewHolder friendViewHolder, int position) {
        final FriendItemResponse friend = friendList.get(position);
        String username = friend.getName();
        String email = friend.getEmail();

        friendViewHolder.username.setText(username);
        friendViewHolder.email.setText(email);

        friendViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                CheckBox checkBox = (CheckBox) view;

                // check if its checked or not
                if (checkBox.isChecked()) {
                    checkedFriendList.add(friendList.get(pos));

                } else if (!checkBox.isChecked()){
                    checkedFriendList.remove(friendList.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

}

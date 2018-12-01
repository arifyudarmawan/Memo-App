package com.example.pradiptaagus.app_project4.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pradiptaagus.app_project4.Model.FriendItemResponse;
import com.example.pradiptaagus.app_project4.R;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private List<FriendItemResponse> friendList;

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView username, email;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tv_username);
            email = itemView.findViewById(R.id.tv_email);
        }
    }

    public FriendAdapter(List<FriendItemResponse> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_friend_item_recycler_view, viewGroup, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.FriendViewHolder friendViewHolder, int position) {
        FriendItemResponse friend = friendList.get(position);
        String username = friend.getName();
        String email = friend.getEmail();

        friendViewHolder.username.setText(username);
        friendViewHolder.email.setText(email);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


}

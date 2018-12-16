package com.example.pradiptaagus.app_project4.Model;

import com.google.gson.annotations.SerializedName;

public class IsFriendResponse{

	@SerializedName("friend_id")
	private int friendId;

	public void setFriendId(int friendId){
		this.friendId = friendId;
	}

	public int getFriendId(){
		return friendId;
	}

	@Override
 	public String toString(){
		return 
			"IsFriendResponse{" + 
			"friend_id = '" + friendId + '\'' + 
			"}";
		}
}
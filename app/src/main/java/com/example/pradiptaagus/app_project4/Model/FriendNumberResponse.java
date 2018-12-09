package com.example.pradiptaagus.app_project4.Model;

import com.google.gson.annotations.SerializedName;

public class FriendNumberResponse{

	@SerializedName("friend")
	private int friend;

	public void setFriend(int friend){
		this.friend = friend;
	}

	public int getFriend(){
		return friend;
	}

	@Override
 	public String toString(){
		return 
			"FriendNumberResponse{" + 
			"friend = '" + friend + '\'' + 
			"}";
		}
}
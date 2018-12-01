package com.example.pradiptaagus.app_project4.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FriendResponse{

	@SerializedName("data")
	private List<FriendItemResponse> data;

	public void setData(List<FriendItemResponse> data){
		this.data = data;
	}

	public List<FriendItemResponse> getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"FriendResponse{" + 
			"data = '" + data + '\'' + 
			"}";
		}
}
package com.example.pradiptaagus.app_project4.Model;

import com.google.gson.annotations.SerializedName;

public class AddFriendResponse{

	@SerializedName("message")
	private String message;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	@Override
 	public String toString(){
		return 
			"AddFriendResponse{" +
			",message = '" + message + '\'' + 
			"}";
		}
}
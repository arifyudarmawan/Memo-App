package com.example.pradiptaagus.app_project4.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginResponse{

	@SerializedName("message")
	private String message;

	@SerializedName("account")
	private List<LoginItemResponse> account;

	@SerializedName("token")
	private String token;

	@SerializedName("status")
	private boolean status;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setAccount(List<LoginItemResponse> account){
		this.account = account;
	}

	public List<LoginItemResponse> getAccount(){
		return account;
	}

	public void setToken(String token){
		this.token = token;
	}

	public String getToken(){
		return token;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"LoginResponse{" + 
			"message = '" + message + '\'' + 
			",account = '" + account + '\'' + 
			",token = '" + token + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}
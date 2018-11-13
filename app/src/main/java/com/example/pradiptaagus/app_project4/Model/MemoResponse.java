package com.example.pradiptaagus.app_project4.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MemoResponse {

	@SerializedName("data")
	private List<MemoItemResponse> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<MemoItemResponse> data){
		this.data = data;
	}

	public List<MemoItemResponse> getData(){
		return data;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
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
			"MemoResponse{" +
			"data = '" + data + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}
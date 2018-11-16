package com.example.pradiptaagus.app_project4.Model;

import com.google.gson.annotations.SerializedName;

public class DeleteMemoResponse{

	@SerializedName("create")
	private DeleteMemoItemResponse create;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setCreate(DeleteMemoItemResponse create){
		this.create = create;
	}

	public DeleteMemoItemResponse getCreate(){
		return create;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
 	public String toString(){
		return 
			"DeleteMemoResponse{" + 
			"create = '" + create + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}
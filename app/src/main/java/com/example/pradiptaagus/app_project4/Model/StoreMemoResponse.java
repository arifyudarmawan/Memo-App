package com.example.pradiptaagus.app_project4.Model;

public class StoreMemoResponse {
	private StoreMemoItemResponse memoResponseItem;
	private String message;

	public void setMemoResponseItem(StoreMemoItemResponse memoResponseItem){
		this.memoResponseItem = memoResponseItem;
	}

	public StoreMemoItemResponse getMemoResponseItem(){
		return memoResponseItem;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	@Override
 	public String toString(){
		return 
			"StoreMemoResponse{" +
			"memoResponseItem = '" + memoResponseItem + '\'' +
			",message = '" + message + '\'' + 
			"}";
		}
}

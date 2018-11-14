package com.example.pradiptaagus.app_project4.Model;

public class LogoutResponse{
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
			"LogoutResponse{" + 
			"message = '" + message + '\'' + 
			"}";
		}
}

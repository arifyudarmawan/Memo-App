package com.example.pradiptaagus.app_project4.Model;

public class UpdateMemoResponse{
	private UpdateMemoItemResponse updateMemoItemResponse;
	private String message;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	private boolean status;

	public void setUpdateMemoItemResponse(UpdateMemoItemResponse updateMemoItemResponse){
		this.updateMemoItemResponse = updateMemoItemResponse;
	}

	public UpdateMemoItemResponse getUpdateMemoItemResponse(){
		return updateMemoItemResponse;
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
			"UpdateMemoResponse{" + 
			"updateMemoItemResponse = '" + updateMemoItemResponse + '\'' +
			",message = '" + message + '\'' + 
			"}";
		}
}

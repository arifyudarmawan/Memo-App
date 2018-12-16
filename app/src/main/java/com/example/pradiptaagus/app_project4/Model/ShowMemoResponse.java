package com.example.pradiptaagus.app_project4.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShowMemoResponse{

	@SerializedName("recipient")
	private List<RecipientItem> recipient;

	@SerializedName("memo")
	private Memo memo;

	public void setRecipient(List<RecipientItem> recipient){
		this.recipient = recipient;
	}

	public List<RecipientItem> getRecipient(){
		return recipient;
	}

	public void setMemo(Memo memo){
		this.memo = memo;
	}

	public Memo getMemo(){
		return memo;
	}

	@Override
 	public String toString(){
		return 
			"ShowMemoResponse{" + 
			"recipient = '" + recipient + '\'' + 
			",memo = '" + memo + '\'' + 
			"}";
		}
}
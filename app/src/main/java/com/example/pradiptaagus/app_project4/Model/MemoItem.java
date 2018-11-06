package com.example.pradiptaagus.app_project4.Model;

import com.google.gson.annotations.SerializedName;

public class MemoItem {

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("detail")
	private String detail;

	@SerializedName("title")
	private String title;

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setDetail(String detail){
		this.detail = detail;
	}

	public String getDetail(){
		return detail;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	@Override
 	public String toString(){
		return 
			"MemoItem{" +
			"updated_at = '" + updatedAt + '\'' + 
			",user_id = '" + userId + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",id = '" + id + '\'' + 
			",detail = '" + detail + '\'' + 
			",title = '" + title + '\'' + 
			"}";
		}
}
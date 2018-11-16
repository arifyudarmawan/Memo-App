package com.example.pradiptaagus.app_project4.Model;

import com.google.gson.annotations.SerializedName;

public class DeleteMemoItemResponse {

	@SerializedName("0")
	private String jsonMember0;

	@SerializedName("method")
	private String method;

	@SerializedName("href")
	private String href;

	@SerializedName("params")
	private String params;

	public void setJsonMember0(String jsonMember0){
		this.jsonMember0 = jsonMember0;
	}

	public String getJsonMember0(){
		return jsonMember0;
	}

	public void setMethod(String method){
		this.method = method;
	}

	public String getMethod(){
		return method;
	}

	public void setHref(String href){
		this.href = href;
	}

	public String getHref(){
		return href;
	}

	public void setParams(String params){
		this.params = params;
	}

	public String getParams(){
		return params;
	}

	@Override
 	public String toString(){
		return 
			"DeleteMemoItemResponse{" +
			"0 = '" + jsonMember0 + '\'' + 
			",method = '" + method + '\'' + 
			",href = '" + href + '\'' + 
			",params = '" + params + '\'' + 
			"}";
		}
}
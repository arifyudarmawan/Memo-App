package com.example.pradiptaagus.app_project4.Model;

public class StoreMemoItemResponse {
	private String method;
	private String href;
	private String params;

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
			"StoreMemoItemResponse{" +
			"method = '" + method + '\'' + 
			",href = '" + href + '\'' + 
			",params = '" + params + '\'' + 
			"}";
		}
}

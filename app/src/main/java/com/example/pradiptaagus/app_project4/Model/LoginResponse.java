package com.example.pradiptaagus.app_project4.Model;

public class LoginResponse {
    private String token;
    private boolean status;
    private String message;

    // method getter and setter

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}

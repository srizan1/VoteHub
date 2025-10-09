package com.voting.system.dto;

import com.voting.system.model.Login;

public class LoginResponse {
    private String message;
    private Login.LoginType loginType;
    private Long userId;
    private String phoneNumber;

    public LoginResponse() {}

    public LoginResponse(String message, Login.LoginType loginType, Long userId, String phoneNumber) {
        this.message = message;
        this.loginType = loginType;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Login.LoginType getLoginType() { return loginType; }
    public void setLoginType(Login.LoginType loginType) { this.loginType = loginType; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}

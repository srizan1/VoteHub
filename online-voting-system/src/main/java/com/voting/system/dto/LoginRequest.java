package com.voting.system.dto;

import com.voting.system.model.Login;

public class LoginRequest {
    private String phoneNumber;
    private String password;
    private Login.LoginType loginType;  // NEW: Required for login

    public LoginRequest() {}

    public LoginRequest(String phoneNumber, String password, Login.LoginType loginType) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.loginType = loginType;
    }

    // Getters and setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Login.LoginType getLoginType() { return loginType; }
    public void setLoginType(Login.LoginType loginType) { this.loginType = loginType; }
}
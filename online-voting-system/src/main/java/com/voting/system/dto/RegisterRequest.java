package com.voting.system.dto;

import com.voting.system.model.Login;

public class RegisterRequest {
    private String phoneNumber;
    private String password;
    private String name;
    private Login.LoginType loginType;

    public RegisterRequest() {}

    public RegisterRequest(String phoneNumber, String password, String name, Login.LoginType loginType) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.loginType = loginType;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Login.LoginType getLoginType() { return loginType; }
    public void setLoginType(Login.LoginType loginType) { this.loginType = loginType; }
}

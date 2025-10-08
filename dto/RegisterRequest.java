package com.voting.system.dto;

import com.voting.system.model.Login;

public class RegisterRequest {
    private String username;
    private String password;
    private Login.LoginType loginType;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, Login.LoginType loginType) {
        this.username = username;
        this.password = password;
        this.loginType = loginType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Login.LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(Login.LoginType loginType) {
        this.loginType = loginType;
    }
}
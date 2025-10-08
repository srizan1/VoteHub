package com.voting.system.dto;

import com.voting.system.model.Login;

public class LoginResponse {
    private String message;
    private Login.LoginType loginType;
    private Long userId;
    private String username;

    public LoginResponse() {}
    public LoginResponse(String message, Login.LoginType loginType, Long userId, String username) {
        this.message = message;
        this.loginType = loginType;
        this.userId = userId;
        this.username = username;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Login.LoginType getLoginType() { return loginType; }
    public void setLoginType(Login.LoginType loginType) { this.loginType = loginType; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}

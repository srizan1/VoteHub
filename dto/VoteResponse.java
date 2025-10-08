package com.voting.system.dto;

public class VoteResponse {
    private String message;
    private boolean success;

    public VoteResponse() {}
    public VoteResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}

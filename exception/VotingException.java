package com.voting.system.exception;

public class VotingException extends RuntimeException {
    private final int statusCode;

    public VotingException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

package com.voting.system.exception;

public class RoomAccessException extends RuntimeException {
    public RoomAccessException(String message) {
        super(message);
    }
}

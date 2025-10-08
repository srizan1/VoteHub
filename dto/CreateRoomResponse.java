package com.voting.system.dto;

public class CreateRoomResponse {
    private String roomId;
    private String roomName;
    private String message;

    public CreateRoomResponse() {}
    public CreateRoomResponse(String roomId, String roomName, String message) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.message = message;
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

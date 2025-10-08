package com.voting.system.dto;

public class JoinRoomRequest {
    private String roomId;
    private Long userId;

    public JoinRoomRequest() {}

    public JoinRoomRequest(String roomId, Long userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
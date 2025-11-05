package com.voting.system.dto;

public class VoteRequest {
    private String roomId;
    private Long userId;
    private String partyName;

    public VoteRequest() {}

    public VoteRequest(String roomId, Long userId, String partyName) {
        this.roomId = roomId;
        this.userId = userId;
        this.partyName = partyName;
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

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
}
package com.voting.system.dto;

import java.util.List;

public class UserRoomsListResponse {
    private Long userId;
    private String phoneNumber;
    private List<String> roomIds;

    public UserRoomsListResponse() {}

    public UserRoomsListResponse(Long userId, String phoneNumber, List<String> roomIds) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.roomIds = roomIds;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public List<String> getRoomIds() { return roomIds; }
    public void setRoomIds(List<String> roomIds) { this.roomIds = roomIds; }
}
package com.voting.system.dto;

// DTO for removing a user from room (admin action)
public class RemoveUserRequest {
    private String roomId;
    private Long adminId;
    private Long userIdToRemove;

    public RemoveUserRequest() {}

    public RemoveUserRequest(String roomId, Long adminId, Long userIdToRemove) {
        this.roomId = roomId;
        this.adminId = adminId;
        this.userIdToRemove = userIdToRemove;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Long getUserIdToRemove() {
        return userIdToRemove;
    }

    public void setUserIdToRemove(Long userIdToRemove) {
        this.userIdToRemove = userIdToRemove;
    }
}

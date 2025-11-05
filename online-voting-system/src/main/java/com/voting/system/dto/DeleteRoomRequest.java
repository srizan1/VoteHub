package com.voting.system.dto;

// DTO for deleting a room (admin action)
public class DeleteRoomRequest {
    private String roomId;
    private Long adminId;

    public DeleteRoomRequest() {}

    public DeleteRoomRequest(String roomId, Long adminId) {
        this.roomId = roomId;
        this.adminId = adminId;
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
}

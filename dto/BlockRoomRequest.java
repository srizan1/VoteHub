package com.voting.system.dto;

public class BlockRoomRequest {
    private String roomId;
    private Long adminId;
    private Boolean blocked;

    public BlockRoomRequest() {}

    public BlockRoomRequest(String roomId, Long adminId, Boolean blocked) {
        this.roomId = roomId;
        this.adminId = adminId;
        this.blocked = blocked;
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    public Boolean getBlocked() { return blocked; }
    public void setBlocked(Boolean blocked) { this.blocked = blocked; }
}

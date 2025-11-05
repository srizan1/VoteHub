package com.voting.system.dto;

public class BlockRoomRequest {
    private String roomId;
    private Long adminId;
    private Integer blocked;  // 0 = Unblock, 1 = Block

    public BlockRoomRequest() {}

    public BlockRoomRequest(String roomId, Long adminId, Integer blocked) {
        this.roomId = roomId;
        this.adminId = adminId;
        this.blocked = blocked;
    }

    // Getters and setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Integer getBlocked() { return blocked; }
    public void setBlocked(Integer blocked) { this.blocked = blocked; }
}

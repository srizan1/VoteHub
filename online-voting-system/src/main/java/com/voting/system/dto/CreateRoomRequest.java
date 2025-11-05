package com.voting.system.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CreateRoomRequest {
    private String roomName;
    private Long adminId;
    private List<String> partyNames;
    private LocalDateTime votingStartTime;
    private LocalDateTime votingEndTime;

    public CreateRoomRequest() {}

    public CreateRoomRequest(String roomName, Long adminId, List<String> partyNames,
                             LocalDateTime votingStartTime, LocalDateTime votingEndTime) {
        this.roomName = roomName;
        this.adminId = adminId;
        this.partyNames = partyNames;
        this.votingStartTime = votingStartTime;
        this.votingEndTime = votingEndTime;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public List<String> getPartyNames() {
        return partyNames;
    }

    public void setPartyNames(List<String> partyNames) {
        this.partyNames = partyNames;
    }

    public LocalDateTime getVotingStartTime() {
        return votingStartTime;
    }

    public void setVotingStartTime(LocalDateTime votingStartTime) {
        this.votingStartTime = votingStartTime;
    }

    public LocalDateTime getVotingEndTime() {
        return votingEndTime;
    }

    public void setVotingEndTime(LocalDateTime votingEndTime) {
        this.votingEndTime = votingEndTime;
    }
}
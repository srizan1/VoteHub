package com.voting.system.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RoomDetailsResponse {
    private String roomId;
    private String roomName;
    private Integer totalRegistered;
    private LocalDateTime votingStartTime;
    private LocalDateTime votingEndTime;
    private List<String> partyNames;
    private Map<String, Integer> currentVotes;
    private Boolean isActive;

    public RoomDetailsResponse() {}
    public RoomDetailsResponse(String roomId, String roomName, Integer totalRegistered,
                               LocalDateTime votingStartTime, LocalDateTime votingEndTime,
                               List<String> partyNames, Map<String, Integer> currentVotes,
                               Boolean isActive) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.totalRegistered = totalRegistered;
        this.votingStartTime = votingStartTime;
        this.votingEndTime = votingEndTime;
        this.partyNames = partyNames;
        this.currentVotes = currentVotes;
        this.isActive = isActive;
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public Integer getTotalRegistered() { return totalRegistered; }
    public void setTotalRegistered(Integer totalRegistered) { this.totalRegistered = totalRegistered; }
    public LocalDateTime getVotingStartTime() { return votingStartTime; }
    public void setVotingStartTime(LocalDateTime votingStartTime) { this.votingStartTime = votingStartTime; }
    public LocalDateTime getVotingEndTime() { return votingEndTime; }
    public void setVotingEndTime(LocalDateTime votingEndTime) { this.votingEndTime = votingEndTime; }
    public List<String> getPartyNames() { return partyNames; }
    public void setPartyNames(List<String> partyNames) { this.partyNames = partyNames; }
    public Map<String, Integer> getCurrentVotes() { return currentVotes; }
    public void setCurrentVotes(Map<String, Integer> currentVotes) { this.currentVotes = currentVotes; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

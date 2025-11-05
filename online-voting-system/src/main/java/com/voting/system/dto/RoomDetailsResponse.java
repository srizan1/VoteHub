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
    private Integer isActive;   // 0 = Inactive, 1 = Active
    private Integer isBlocked;  // 0 = Not Blocked, 1 = Blocked
    private Boolean hasVoted;   // true if user has voted in this room
    private Integer totalVotes; // total number of votes cast across all parties

    public RoomDetailsResponse() {}

    public RoomDetailsResponse(String roomId, String roomName, Integer totalRegistered,
                               LocalDateTime votingStartTime, LocalDateTime votingEndTime,
                               List<String> partyNames, Map<String, Integer> currentVotes,
                               Integer isActive, Integer isBlocked) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.totalRegistered = totalRegistered;
        this.votingStartTime = votingStartTime;
        this.votingEndTime = votingEndTime;
        this.partyNames = partyNames;
        this.currentVotes = currentVotes;
        this.isActive = isActive;
        this.isBlocked = isBlocked;
    }

    // Getters and setters
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

    public Integer getIsActive() { return isActive; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }

    public Integer getIsBlocked() { return isBlocked; }
    public void setIsBlocked(Integer isBlocked) { this.isBlocked = isBlocked; }

    public Boolean getHasVoted() { return hasVoted; }
    public void setHasVoted(Boolean hasVoted) { this.hasVoted = hasVoted; }

    public Integer getTotalVotes() { return totalVotes; }
    public void setTotalVotes(Integer totalVotes) { this.totalVotes = totalVotes; }
}
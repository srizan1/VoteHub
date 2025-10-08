package com.voting.system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @Column(unique = true, nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private Long adminId;

    private Integer totalRegistered = 0;

    @Column(nullable = false)
    private LocalDateTime votingStartTime;

    @Column(nullable = false)
    private LocalDateTime votingEndTime;

    @Column(columnDefinition = "JSON")
    private String partyVotesJson;

    @Column(columnDefinition = "JSON")
    private String partyNamesJson;

    private Boolean isActive = true;

    public Room() {}

    public Room(String roomId, String roomName, Long adminId, Integer totalRegistered,
                LocalDateTime votingStartTime, LocalDateTime votingEndTime,
                String partyVotesJson, String partyNamesJson, Boolean isActive) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.adminId = adminId;
        this.totalRegistered = totalRegistered;
        this.votingStartTime = votingStartTime;
        this.votingEndTime = votingEndTime;
        this.partyVotesJson = partyVotesJson;
        this.partyNamesJson = partyNamesJson;
        this.isActive = isActive;
    }

    // Getters and setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public Integer getTotalRegistered() {
        return totalRegistered;
    }

    public void setTotalRegistered(Integer totalRegistered) {
        this.totalRegistered = totalRegistered;
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

    public String getPartyVotesJson() {
        return partyVotesJson;
    }

    public void setPartyVotesJson(String partyVotesJson) {
        this.partyVotesJson = partyVotesJson;
    }

    public String getPartyNamesJson() {
        return partyNamesJson;
    }

    public void setPartyNamesJson(String partyNamesJson) {
        this.partyNamesJson = partyNamesJson;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
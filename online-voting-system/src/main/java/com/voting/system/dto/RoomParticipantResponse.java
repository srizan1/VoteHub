package com.voting.system.dto;

import java.time.LocalDateTime;

public class RoomParticipantResponse {
    private Long userId;
    private String name;
    private String phoneNumber;
    private Boolean hasVoted;
    private LocalDateTime votedAt;

    public RoomParticipantResponse() {}

    public RoomParticipantResponse(Long userId, String name, String phoneNumber, Boolean hasVoted, LocalDateTime votedAt) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.hasVoted = hasVoted;
        this.votedAt = votedAt;
    }

    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Boolean getHasVoted() { return hasVoted; }
    public void setHasVoted(Boolean hasVoted) { this.hasVoted = hasVoted; }

    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}

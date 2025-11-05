package com.voting.system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vote_records")
public class VoteRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String roomId;

    // CHANGED: Remove nullable = false, allow NULL when user hasn't voted yet
    @Column
    private LocalDateTime votedAt;

    // 0 = Not Voted, 1 = Has Voted
    @Column(nullable = false)
    private Integer hasVoted = 0;

    public VoteRecord() {}

    public VoteRecord(Long id, Long userId, String roomId, LocalDateTime votedAt, Integer hasVoted) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.votedAt = votedAt;
        this.hasVoted = hasVoted;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }

    public Integer getHasVoted() { return hasVoted; }
    public void setHasVoted(Integer hasVoted) { this.hasVoted = hasVoted; }
}
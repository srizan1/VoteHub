package com.voting.system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(columnDefinition = "JSON")
    private String roomsJson;

    public User() {}

    public User(Long id, String username, String roomsJson) {
        this.id = id;
        this.username = username;
        this.roomsJson = roomsJson;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRoomsJson() { return roomsJson; }
    public void setRoomsJson(String roomsJson) { this.roomsJson = roomsJson; }
}
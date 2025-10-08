package com.voting.system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "administrators")
public class Administrator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(columnDefinition = "JSON")
    private String createdRoomsJson;

    public Administrator() {}

    public Administrator(Long id, String username, String createdRoomsJson) {
        this.id = id;
        this.username = username;
        this.createdRoomsJson = createdRoomsJson;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreatedRoomsJson() {
        return createdRoomsJson;
    }

    public void setCreatedRoomsJson(String createdRoomsJson) {
        this.createdRoomsJson = createdRoomsJson;
    }
}
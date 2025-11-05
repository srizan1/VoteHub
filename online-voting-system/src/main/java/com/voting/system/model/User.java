package com.voting.system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "JSON")
    private String roomsJson;

    public User() {}

    public User(Long id, String phoneNumber, String name, String roomsJson) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.roomsJson = roomsJson;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRoomsJson() { return roomsJson; }
    public void setRoomsJson(String roomsJson) { this.roomsJson = roomsJson; }
}

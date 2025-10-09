package com.voting.system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "administrators")
public class Administrator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String phoneNumber;

    @Column(columnDefinition = "JSON")
    private String createdRoomsJson;

    public Administrator() {}

    public Administrator(Long id, String phoneNumber, String createdRoomsJson) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.createdRoomsJson = createdRoomsJson;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getCreatedRoomsJson() { return createdRoomsJson; }
    public void setCreatedRoomsJson(String createdRoomsJson) { this.createdRoomsJson = createdRoomsJson; }
}

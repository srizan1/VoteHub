package com.voting.system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 4)
    private String otpCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer isVerified = 0; // 0 = Not Verified, 1 = Verified

    public Otp() {}

    public Otp(String phoneNumber, String otpCode, LocalDateTime createdAt) {
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
        this.createdAt = createdAt;
        this.isVerified = 0;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getIsVerified() { return isVerified; }
    public void setIsVerified(Integer isVerified) { this.isVerified = isVerified; }
}
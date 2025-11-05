package com.voting.system.dto;

// Request to send OTP
public class SendOtpRequest {
    private String phoneNumber;

    public SendOtpRequest() {}

    public SendOtpRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
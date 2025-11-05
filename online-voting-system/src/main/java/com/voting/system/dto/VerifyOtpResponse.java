package com.voting.system.dto;

// Response after verifying OTP
public class VerifyOtpResponse {
    private String message;
    private boolean verified;
    private String phoneNumber;

    public VerifyOtpResponse() {}

    public VerifyOtpResponse(String message, boolean verified, String phoneNumber) {
        this.message = message;
        this.verified = verified;
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
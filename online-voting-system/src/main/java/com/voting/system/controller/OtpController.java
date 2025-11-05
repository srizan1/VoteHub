
package com.voting.system.controller;

import com.voting.system.dto.*;
import com.voting.system.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "*")
public class OtpController {

    @Autowired
    private OtpService otpService;

    // Send OTP
    @PostMapping("/send")
    public ResponseEntity<SendOtpResponse> sendOtp(@RequestBody SendOtpRequest request) {
        SendOtpResponse response = otpService.sendOtp(request);
        return ResponseEntity.ok(response);
    }

    // Verify OTP
    @PostMapping("/verify")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        VerifyOtpResponse response = otpService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }
}
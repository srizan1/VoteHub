package com.voting.system.service;

import com.voting.system.dto.*;
import com.voting.system.exception.*;
import com.voting.system.model.Otp;
import com.voting.system.repository.OtpRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    // Twilio Configuration (add these to application.properties)
    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");
    private static final int OTP_LENGTH = 4;

    // Generate and send OTP via Twilio
    public SendOtpResponse sendOtp(SendOtpRequest request) {
        try {
            // Validate phone number
            if (!isValidPhoneNumber(request.getPhoneNumber())) {
                throw new InvalidRequestException("Invalid phone number format. Must be 10-15 digits.");
            }

            // Generate random 4-digit OTP
            String otpCode = generateOtp();

            // Set creation time
            LocalDateTime now = LocalDateTime.now();

            // Save OTP to database FIRST
            Otp otp = new Otp(request.getPhoneNumber(), otpCode, now);
            otpRepository.save(otp);

            System.out.println("✅ OTP saved to database: " + otpCode + " for phone: " + request.getPhoneNumber());

            // Initialize Twilio
            Twilio.init(twilioAccountSid, twilioAuthToken);

            // Format phone number for Twilio (add country code if not present)
            String formattedPhoneNumber = formatPhoneNumber(request.getPhoneNumber());

            // Send SMS via Twilio
            Message message = Message.creator(
                    new PhoneNumber(formattedPhoneNumber), // To
                    new PhoneNumber(twilioPhoneNumber),    // From (your Twilio number)
                    "Your OTP code is: " + otpCode          // Message body
            ).create();

            System.out.println("✅ OTP sent via Twilio. SID: " + message.getSid());

            return new SendOtpResponse(
                    "OTP sent successfully to " + request.getPhoneNumber(),
                    request.getPhoneNumber()
            );

        } catch (InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            // If Twilio fails, the OTP is already in database, so user can still verify
            System.err.println("❌ Twilio Error: " + e.getMessage());
            throw new RuntimeException("Error sending OTP via SMS: " + e.getMessage(), e);
        }
    }

    // Verify OTP from database
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
        try {
            // Validate phone number
            if (!isValidPhoneNumber(request.getPhoneNumber())) {
                throw new InvalidRequestException("Invalid phone number format.");
            }

            // Validate OTP code
            if (request.getOtpCode() == null || request.getOtpCode().trim().isEmpty()) {
                throw new InvalidRequestException("OTP code cannot be empty.");
            }

            // Find latest OTP from database for this phone number
            Otp otp = otpRepository.findFirstByPhoneNumberOrderByCreatedAtDesc(
                    request.getPhoneNumber()
            ).orElseThrow(() -> new InvalidRequestException("No OTP found for this phone number. Please request a new OTP."));

            // Check if already verified (one-time use)
            if (otp.getIsVerified() == 1) {
                throw new InvalidRequestException("OTP already used. Please request a new OTP.");
            }

            // Match OTP code with database
            if (!otp.getOtpCode().equals(request.getOtpCode())) {
                throw new InvalidCredentialsException("Invalid OTP code. Please try again.");
            }

            // Mark as verified in database
            otp.setIsVerified(1);
            otpRepository.save(otp);

            System.out.println("✅ OTP Verified Successfully for: " + request.getPhoneNumber());

            return new VerifyOtpResponse(
                    "OTP verified successfully",
                    true,
                    request.getPhoneNumber()
            );

        } catch (InvalidRequestException | InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error verifying OTP: " + e.getMessage(), e);
        }
    }

    // Generate random 4-digit OTP
    private String generateOtp() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // 4-digit number (1000-9999)
        return String.valueOf(otp);
    }

    // Format phone number for Twilio (add +91 for India, +1 for US, etc.)
    private String formatPhoneNumber(String phoneNumber) {
        // If phone number doesn't start with +, add country code
        if (!phoneNumber.startsWith("+")) {
            // Assuming India (+91) - change this based on your country
            return "+91" + phoneNumber;
        }
        return phoneNumber;
    }

    // Validate phone number format
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }
}
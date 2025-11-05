package com.voting.system.repository;

import com.voting.system.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    // Find latest OTP for a phone number
    Optional<Otp> findFirstByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
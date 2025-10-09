package com.voting.system.service;

import com.voting.system.dto.*;
import com.voting.system.exception.*;
import com.voting.system.model.*;
import com.voting.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// CORRECT IMPORT FOR BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.regex.Pattern;

@Service
public class AuthService {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    public LoginResponse login(LoginRequest request) {
        try {
            // Validate phone number format
            if (!isValidPhoneNumber(request.getPhoneNumber())) {
                throw new InvalidRequestException("Invalid phone number format. Must be 10-15 digits.");
            }

            // Find login
            Login login = loginRepository.findByPhoneNumber(request.getPhoneNumber())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid phone number or password"));

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), login.getPasswordHash())) {
                throw new InvalidCredentialsException("Invalid phone number or password");
            }

            // Get user ID
            Long userId;
            if (login.getLoginType() == Login.LoginType.VOTER) {
                User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                        .orElseThrow(() -> new ResourceNotFoundException("User account not found"));
                userId = user.getId();
            } else {
                Administrator admin = administratorRepository.findByPhoneNumber(request.getPhoneNumber())
                        .orElseThrow(() -> new ResourceNotFoundException("Administrator account not found"));
                userId = admin.getId();
            }

            return new LoginResponse(
                    "Login successful",
                    login.getLoginType(),
                    userId,
                    request.getPhoneNumber()
            );

        } catch (InvalidCredentialsException | InvalidRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error during login: " + e.getMessage(), e);
        }
    }

    public LoginResponse register(RegisterRequest request) {
        try {
            // Validate phone number format
            if (!isValidPhoneNumber(request.getPhoneNumber())) {
                throw new InvalidRequestException("Invalid phone number format. Must be 10-15 digits.");
            }

            // Validate password strength
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                throw new InvalidRequestException("Password must be at least 6 characters long");
            }

            // Check if phone number already exists
            if (loginRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new DuplicateResourceException("Phone number already registered");
            }

            // Create login
            Login login = new Login();
            login.setPhoneNumber(request.getPhoneNumber());
            login.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            login.setLoginType(request.getLoginType());
            loginRepository.save(login);

            // Create corresponding user/admin account
            Long userId;
            if (request.getLoginType() == Login.LoginType.VOTER) {
                User user = new User();
                user.setPhoneNumber(request.getPhoneNumber());
                user.setRoomsJson("[]");
                user = userRepository.save(user);
                userId = user.getId();
            } else {
                Administrator admin = new Administrator();
                admin.setPhoneNumber(request.getPhoneNumber());
                admin.setCreatedRoomsJson("[]");
                admin = administratorRepository.save(admin);
                userId = admin.getId();
            }

            return new LoginResponse(
                    "Registration successful",
                    request.getLoginType(),
                    userId,
                    request.getPhoneNumber()
            );

        } catch (DuplicateResourceException | InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error during registration: " + e.getMessage(), e);
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }
}
package com.voting.system.service;

import com.voting.system.model.Login;
import com.voting.system.model.User;
import com.voting.system.model.Administrator;
import com.voting.system.repository.LoginRepository;
import com.voting.system.repository.UserRepository;
import com.voting.system.repository.AdministratorRepository;
import com.voting.system.dto.LoginRequest;
import com.voting.system.dto.LoginResponse;
import com.voting.system.dto.RegisterRequest;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final LoginRepository loginRepository;
    private final UserRepository userRepository;
    private final AdministratorRepository administratorRepository;

    public AuthService(LoginRepository loginRepository,
                       UserRepository userRepository,
                       AdministratorRepository administratorRepository) {
        this.loginRepository = loginRepository;
        this.userRepository = userRepository;
        this.administratorRepository = administratorRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Optional<Login> loginOpt = loginRepository.findByUsername(loginRequest.getUsername());

        if (loginOpt.isEmpty()) {
            return new LoginResponse("User not found", null, null, null);
        }

        Login login = loginOpt.get();

        // Simple password comparison (without encryption for now)
        if (!loginRequest.getPassword().equals(login.getPasswordHash())) {
            return new LoginResponse("Invalid password", null, null, null);
        }

        Long userId = null;
        if (login.getLoginType() == Login.LoginType.VOTER) {
            Optional<User> userOpt = userRepository.findByUsername(login.getUsername());
            userId = userOpt.map(User::getId).orElse(null);
        } else if (login.getLoginType() == Login.LoginType.ADMINISTRATOR) {
            Optional<Administrator> adminOpt = administratorRepository.findByUsername(login.getUsername());
            userId = adminOpt.map(Administrator::getId).orElse(null);
        }

        return new LoginResponse("Login successful", login.getLoginType(), userId, login.getUsername());
    }

    public LoginResponse register(RegisterRequest registerRequest) {
        if (loginRepository.existsByUsername(registerRequest.getUsername())) {
            return new LoginResponse("Username already exists", null, null, null);
        }

        // Store plain text password (you can add encryption later)
        String passwordHash = registerRequest.getPassword();
        Login login = new Login(null, registerRequest.getUsername(), passwordHash, registerRequest.getLoginType());
        loginRepository.save(login); // Removed unused variable

        Long userId = null;
        if (registerRequest.getLoginType() == Login.LoginType.VOTER) {
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setRoomsJson("[]");
            User savedUser = userRepository.save(user);
            userId = savedUser.getId();
        } else if (registerRequest.getLoginType() == Login.LoginType.ADMINISTRATOR) {
            Administrator admin = new Administrator();
            admin.setUsername(registerRequest.getUsername());
            admin.setCreatedRoomsJson("[]");
            Administrator savedAdmin = administratorRepository.save(admin);
            userId = savedAdmin.getId();
        }

        return new LoginResponse("Registration successful", registerRequest.getLoginType(), userId, registerRequest.getUsername());
    }

    // Kept the method as it might be used later
    public boolean validateUser(Long userId, String username) {
        Optional<Login> loginOpt = loginRepository.findByUsername(username);
        if (loginOpt.isEmpty()) return false;

        Login login = loginOpt.get();
        if (login.getLoginType() == Login.LoginType.VOTER) {
            Optional<User> userOpt = userRepository.findById(userId);
            return userOpt.isPresent() && userOpt.get().getUsername().equals(username);
        } else {
            Optional<Administrator> adminOpt = administratorRepository.findById(userId);
            return adminOpt.isPresent() && adminOpt.get().getUsername().equals(username);
        }
    }
}
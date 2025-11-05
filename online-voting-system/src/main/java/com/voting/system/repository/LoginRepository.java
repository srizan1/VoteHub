package com.voting.system.repository;

import com.voting.system.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
    // Find by phone number AND login type (both together)
    Optional<Login> findByPhoneNumberAndLoginType(String phoneNumber, Login.LoginType loginType);

    // Check if combination exists
    boolean existsByPhoneNumberAndLoginType(String phoneNumber, Login.LoginType loginType);
}
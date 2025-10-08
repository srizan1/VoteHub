package com.voting.system.repository;

import com.voting.system.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Optional<Administrator> findByUsername(String username);
    boolean existsByUsername(String username);
}

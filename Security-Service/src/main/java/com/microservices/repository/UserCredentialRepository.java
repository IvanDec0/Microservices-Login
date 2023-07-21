package com.microservices.repository;

import com.microservices.entity.UserCredential;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserCredentialRepository extends JpaRepository<UserCredential, Integer> {
    UserCredential findByEmail(String email);
    Boolean existsByEmail(String email);
}
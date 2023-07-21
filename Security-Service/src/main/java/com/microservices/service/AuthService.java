package com.microservices.service;

import com.microservices.dto.RegisterRequest;
import com.microservices.entity.Rol;
import com.microservices.entity.UserCredential;
import com.microservices.repository.RolRepository;
import com.microservices.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialRepository repository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request){
        if (!(request.getEmail().isEmpty()) && repository.existsByEmail(request.getEmail())){
            return "There is already an account registered with the same email";
        }
        List<Rol> roles = new ArrayList<>() {{
            if (rolRepository.existsByName("ROLE_" + (request.getRole()).toUpperCase())){
                add(rolRepository.findByName("ROLE_" + (request.getRole()).toUpperCase()));
            }
            add(rolRepository.findByName("ROLE_USER"));
        }};
        var user = UserCredential.builder()
                .name(request.getName())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .active(true)
                .build();
        repository.save(user);
        return "User registered successfully";
    }

    public String generateToken(String email){
        return jwtService.generateToken(email);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    public UserCredential findUserByEmail(String email) {
        return repository.findByEmail(email);
    }
}

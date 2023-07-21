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
        List<Rol> roles = new ArrayList<>() {{
            add(rolRepository.findByName("ROLE_" + (request.getRole()).toUpperCase())); // TODO: VER BUG ROL NO EXISTENTE CRAHSEA
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
}

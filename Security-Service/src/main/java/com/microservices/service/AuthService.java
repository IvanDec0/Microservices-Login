package com.microservices.service;

import com.microservices.dto.LoginRequest;
import com.microservices.dto.RegisterRequest;
import com.microservices.entity.Rol;
import com.microservices.entity.UserCredential;
import com.microservices.repository.RolRepository;
import com.microservices.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialRepository repository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<String> register(RegisterRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            return new ResponseEntity<>("There is already an account registered with the same email", HttpStatus.CONFLICT);
        }

        List<Rol> roles = new ArrayList<>() {{
            request.getRoles().forEach(role -> {
                if (rolRepository.existsByName("ROLE_" + (role).toUpperCase())) {
                    add(rolRepository.findByName("ROLE_" + (role).toUpperCase()));
                }
            });

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
        //return generateToken(user.getEmail());
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    public ResponseEntity<String> login(LoginRequest user) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        return new ResponseEntity<>(generateToken(user.getEmail()), HttpStatus.OK);

    }

    public String generateToken(String email) {
        Optional<UserCredential> user = repository.findByEmail(email);
        List<String> roles = user.get().getRoles().stream().map(Rol::getName).collect(Collectors.toList());
        return jwtService.generateToken(email, roles);
    }

    public ResponseEntity<String> validateToken(String token) {
        if (token.isEmpty()) {
            return new ResponseEntity<>("Token is mandatory", HttpStatus.BAD_REQUEST);
        }
        try {
            jwtService.validateToken(token);
            return new ResponseEntity<>("Valid token", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Bad Signature", HttpStatus.UNAUTHORIZED);
        }
    }
}

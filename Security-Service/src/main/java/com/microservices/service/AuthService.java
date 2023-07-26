package com.microservices.service;

import com.microservices.dto.LoginRequest;
import com.microservices.dto.RegisterRequest;
import com.microservices.entity.Rol;
import com.microservices.entity.UserCredential;
import com.microservices.repository.RolRepository;
import com.microservices.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SignatureException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialRepository repository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
        //return generateToken(user.getEmail());
        return "User registered successfully";
    }

    public String login(LoginRequest user){
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if (authenticate.isAuthenticated()) {
            return generateToken(user.getEmail());
        } else {
            throw new ResponseStatusException(UNAUTHORIZED, "check you credentials");
        }
    }

    public String generateToken(String email){
        Optional<UserCredential> user = repository.findByEmail(email);
        List<String> roles = user.get().getRoles().stream().map(Rol::getName).collect(Collectors.toList());
        return jwtService.generateToken(email,roles);
    }

    public String validateToken(String token) {
        try {
            jwtService.validateToken(token);
            return "Valid token";
        } catch (RuntimeException e){
            return "Bad Signature";
        }


    }

}

package com.microservices.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.microservices.dto.LoginRequest;
import com.microservices.dto.RegisterRequest;
import com.microservices.entity.Rol;
import com.microservices.entity.UserCredential;
import com.microservices.repository.RolRepository;
import com.microservices.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialRepository repository;
    private final RolRepository rolRepository;
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

        String hashedPassword= BCrypt.withDefaults()
                .hashToString(12, request.getPassword().toCharArray());
        var user = UserCredential.builder()
                .name(request.getName())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(hashedPassword)
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
        return repository.findByEmail(email).orElseThrow();
    }

    public String login(LoginRequest user) {
        UserCredential userDB = repository.findByEmail(user.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(UNAUTHORIZED, "check you credentials")
                );

        boolean verified = BCrypt.verifyer()
                .verify(user.getPassword().toCharArray(), userDB.getPassword())
                .verified;

        if(!verified) {
            throw new ResponseStatusException(UNAUTHORIZED, "check you credentials");
        }

        return generateToken(user.getEmail());

    }
}

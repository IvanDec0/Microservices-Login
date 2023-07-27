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

    public ResponseEntity<String> register(RegisterRequest request){

        if(validations(request)!= null){
            return validations(request);
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
        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }

    public ResponseEntity<String> login(LoginRequest user){

        if(user.getEmail().isEmpty()){
            return new ResponseEntity<>("Email is mandatory", HttpStatus.BAD_REQUEST);
        }
        if(user.getPassword().isEmpty()){
            return new ResponseEntity<>("Password is mandatory", HttpStatus.BAD_REQUEST);
        }

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if (authenticate.isAuthenticated()) {
            return new ResponseEntity<>(generateToken(user.getEmail()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Check you credentials", UNAUTHORIZED);
        }
    }

    public String generateToken(String email){
        Optional<UserCredential> user = repository.findByEmail(email);
        List<String> roles = user.get().getRoles().stream().map(Rol::getName).collect(Collectors.toList());
        return jwtService.generateToken(email,roles);
    }

    public ResponseEntity<String> validateToken(String token) {
        if(token.isEmpty()){
            return new ResponseEntity<>("Token is mandatory", HttpStatus.BAD_REQUEST);
        }
        try {
            jwtService.validateToken(token);
            return new ResponseEntity<>("Valid token", HttpStatus.OK);
        } catch (RuntimeException e){
            return new ResponseEntity<>("Bad Signature", UNAUTHORIZED);
        }
    }

    private ResponseEntity<String> validations(RegisterRequest request){

        if (repository.existsByEmail(request.getEmail())){
            return new ResponseEntity<>("There is already an account registered with the same email", HttpStatus.CONFLICT);
        }

        if(request.getEmail().isEmpty()){
            return new ResponseEntity<>("Email is mandatory", HttpStatus.BAD_REQUEST);
        }
        if(request.getPassword().isEmpty()){
            return new ResponseEntity<>("Password is mandatory", HttpStatus.BAD_REQUEST);
        }
        if(request.getName().isEmpty()){
            return new ResponseEntity<>("Name is mandatory", HttpStatus.BAD_REQUEST);
        }
        if(request.getLastname().isEmpty()){
            return new ResponseEntity<>("Lastname is mandatory", HttpStatus.BAD_REQUEST);
        }
        if(!request.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            return new ResponseEntity<>("Invalid email", HttpStatus.BAD_REQUEST);
        }
        if(request.getPassword().length()>32){
            return new ResponseEntity<>("Password max length is 32 chars", HttpStatus.BAD_REQUEST);
        }
        if(request.getPassword().length()<8){
            return new ResponseEntity<>("Password min length is 8 chars", HttpStatus.BAD_REQUEST);
        }
        if(!request.getPassword().matches(".*[A-Z].*")){
            return new ResponseEntity<>("At least one letter in uppercase is mandatory", HttpStatus.BAD_REQUEST);
        }
        if(!request.getPassword().matches(".*[a-z].*")){
            return new ResponseEntity<>("At least one letter in lowercase is mandatory", HttpStatus.BAD_REQUEST);
        }
        if(!request.getPassword().matches(".*[0-9].*")){
            return new ResponseEntity<>("At least one number is mandatory", HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}

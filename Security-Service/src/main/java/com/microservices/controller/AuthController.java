package com.microservices.controller;

import com.microservices.dto.LoginRequest;
import com.microservices.dto.RegisterRequest;
import com.microservices.service.AuthService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<String> addUser(@RequestBody @Valid RegisterRequest user) {
        return service.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest user) {
        return service.login(user);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@NonNull @RequestParam("token") String token) {
        return service.validateToken(token);
    }
}

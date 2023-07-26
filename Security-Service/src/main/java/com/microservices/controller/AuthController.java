package com.microservices.controller;

import com.microservices.dto.LoginRequest;
import com.microservices.dto.RegisterRequest;
import com.microservices.entity.UserCredential;
import com.microservices.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    public String addUser(@RequestBody RegisterRequest user) {
        return service.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest user) {
        return service.login(user);
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        return service.validateToken(token);
    }
}

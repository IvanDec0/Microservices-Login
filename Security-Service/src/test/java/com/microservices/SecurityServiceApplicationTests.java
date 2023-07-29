package com.microservices;

import com.microservices.dto.RegisterRequest;
import com.microservices.entity.UserCredential;
import com.microservices.repository.RolRepository;
import com.microservices.repository.UserCredentialRepository;
import com.microservices.service.AuthService;
import com.microservices.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

@SpringBootTest
class SecurityServiceApplicationTests {
    private AuthService authService;
    private UserCredentialRepository userCredentialRepository;

    @BeforeEach
    void setup(){
        userCredentialRepository= mock(UserCredentialRepository.class);
        var rolRepository= mock(RolRepository.class);
        var passwordEncoder= mock(PasswordEncoder.class);
        var jwtService= new JwtService();
        var authenticationManager= mock(AuthenticationManager.class);
        authService= new AuthService(userCredentialRepository, rolRepository, passwordEncoder, jwtService, authenticationManager);
    }

    private final RegisterRequest some_valid_registration_request=new RegisterRequest(
            "Bruno", "Mollo",
            "someEmail@gmail.com",
            "paSsWord123", "ADMIN");

    @Test
    void register_user_if_email_is_not_taken_and_input_is_valid(){
        when(userCredentialRepository.existsByEmail(any())).thenReturn(false);

        var response=authService.register(some_valid_registration_request);

        verify(userCredentialRepository, times(1) ).save(any());
        assertEquals(response.getStatusCode(), CREATED);
    }

    @Test
    void dont_save_if_email_is_taken(){
        when(userCredentialRepository.existsByEmail(any())).thenReturn(true);

        var response=authService.register(some_valid_registration_request);

        verify(userCredentialRepository, times(0) ).save(any());
        assertEquals(response.getStatusCode(), CONFLICT);
    }

}

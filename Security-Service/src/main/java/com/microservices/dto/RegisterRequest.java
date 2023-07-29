package com.microservices.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    private String name;
    private String lastname;
    private String email;
    private String password;
    private List<String> roles;
}

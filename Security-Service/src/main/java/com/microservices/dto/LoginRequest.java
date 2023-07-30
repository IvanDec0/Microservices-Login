package com.microservices.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotNull(message = "Email is invalid: Email cannot be null")
    @NotBlank(message = "Email is invalid: Email cannot be blank")
    @NotEmpty(message = "Email is invalid: Email cannot be empty")
    @Email(message = "Email is invalid")
    private String email;

    @NotNull(message = "Password is invalid: Password cannot be null")
    @NotBlank(message = "Password is invalid: Password cannot be blank")
    @NotEmpty(message = "Password is invalid: Password cannot be empty")
    private String password;
}

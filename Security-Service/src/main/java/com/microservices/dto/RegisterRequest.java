package com.microservices.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotNull(message = "Name is invalid: Name cannot be null")
    @NotBlank(message = "Name is invalid: Name cannot be blank")
    @NotEmpty(message = "Name is invalid: Name cannot be empty")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Name is invalid: Name can only contain letters and whitespace")
    private String name;

    @NotNull(message = "Lastname is invalid: Lastname cannot be null")
    @NotBlank(message = "Lastname is invalid: Lastname cannot be blank")
    @NotEmpty(message = "Lastname is invalid: Lastname cannot be empty")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Lastname is invalid: Lastname can only contain letters and whitespace")
    private String lastname;

    @NotNull(message = "Email is invalid: Email cannot be null")
    @NotBlank(message = "Email is invalid: Email cannot be blank")
    @NotEmpty(message = "Email is invalid: Email cannot be empty")
    @Email(message = "Email is invalid")
    private String email;

    @NotNull(message = "Password is invalid: Password cannot be null")
    @NotBlank(message = "Password is invalid: Password cannot be blank")
    @NotEmpty(message = "Password is invalid: Password cannot be empty")
    @Size(min = 8, message = "Password min length is 8 chars")
    @Size(max = 32, message = "Password max length is 32 chars")
    @Pattern(regexp = ".*[A-Z].*", message = "At least one letter in uppercase is mandatory")
    @Pattern(regexp = ".*[a-z].*", message = "At least one letter in lowercase is mandatory")
    @Pattern(regexp = ".*[0-9].*", message = "At least one number is mandatory")
    private String password;

    @NotNull(message = "Roles are invalid: Roles cannot be null")
    private List<String> roles;
}

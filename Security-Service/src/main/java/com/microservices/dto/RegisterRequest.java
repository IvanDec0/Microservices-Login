package com.microservices.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NonNull private String name;
    @NonNull private String lastname;
    @NonNull private String email;
    @NonNull private String password;
    @NonNull private String role;
}

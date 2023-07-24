package com.microservices.util;

import com.microservices.entity.Rol;
import com.microservices.repository.RolRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class CreateRoles implements CommandLineRunner {


    private final RolRepository repository;


    @Override
    public void run(String... args) throws Exception {

 Rol rolAdmin = new Rol("ROLE_ADMIN");
 Rol rolUser = new Rol("ROLE_USER");
 repository.save(rolAdmin);
 repository.save(rolUser);
    }
}

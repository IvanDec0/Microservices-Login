package com.microservices.repository;

import com.microservices.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Rol findByName(String name);

    Boolean existsByName(String name);
}
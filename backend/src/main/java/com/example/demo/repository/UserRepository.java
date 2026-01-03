package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Diese Methode brauchen wir für den Login, 
    // um zu prüfen, ob die E-Mail existiert.
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
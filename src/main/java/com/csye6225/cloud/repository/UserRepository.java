package com.csye6225.cloud.repository;

import com.csye6225.cloud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    User findById(UUID id);
}

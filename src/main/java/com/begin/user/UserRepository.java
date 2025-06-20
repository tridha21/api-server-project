package com.begin.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;


public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}

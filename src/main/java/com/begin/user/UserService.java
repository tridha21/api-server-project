package com.begin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.config.IsNewAwareAuditingHandlerBeanDefinitionParser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
    }

    public User saveUser(User user) {
        // Added logic to avoid spam domains
        if (user.getEmail() != null && user.getEmail().endsWith("@spam.com")) {
            throw new IllegalArgumentException("Spam domains are not allowed");
        }

        if (user.getPassword() != null && user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new IllegalStateException("No users found in database");
        }
        return users;
    }

    public Optional<User> getUserById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        return userRepository.findByEmail(email);
    }

    public void deleteUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User ID does not exist: " + id);
        }
        userRepository.deleteById(id);
    }

}
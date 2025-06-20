package com.begin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Load the HTML UI page (kp.html should be in /resources/templates)
    @GetMapping("/UI_Page")
    public String UI_Page(Model model) {
        model.addAttribute("user", new User());
        return "kp"; // Thymeleaf or JSP page name
    }

    // Handle Signup Form Submission
    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute User user, Model model) {
        Optional<User> existingUser = userService.getUserByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            model.addAttribute("message", "User already exists with this email.");
        } else {
            userService.saveUser(user);
            model.addAttribute("message", "Signup successful! Please login.");
        }

        model.addAttribute("user", new User());
        return "kp";
    }

    // Handle Login Form Submission
    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              Model model) {
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            model.addAttribute("message", "Email not registered.");
        } else if (!userOpt.get().getPassword().equals(password)) {
            model.addAttribute("message", "Incorrect password.");
        } else {
            model.addAttribute("message", "Login successful. Welcome " + userOpt.get().getName() + "!");
            // Optionally redirect to dashboard:
            // return "redirect:/dashboard";
        }

        model.addAttribute("user", new User());
        return "kp";
    }

    // Optional: List all users (for testing only)
    @GetMapping
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Optional: Get user by ID (for testing only)
    @GetMapping("{id}")
    @ResponseBody
    public Optional<User> getUser(@PathVariable int id) {
        return userService.getUserById(id);
    }

    // Optional: Update user
    @PutMapping("{id}")
    @ResponseBody
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            User existing = userOpt.get();
            existing.setName(user.getName());
            existing.setEmail(user.getEmail());
            existing.setPassword(user.getPassword());
            return userService.saveUser(existing);
        }
        return null;
    }

    // Optional: Delete user
    @DeleteMapping("{id}")
    @ResponseBody
    public void deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
    }
}

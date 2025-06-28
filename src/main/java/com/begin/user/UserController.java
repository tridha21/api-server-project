package com.begin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//@Controller
@RestController
@RequestMapping("/users")
public class UserController {



    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }







        @Autowired
    private UserService userService;

    public UserController(UserService userService) {
    }

//    @GetMapping("/UI_Page")
//    public String UI_Page(Model model) {
//        model.addAttribute("user", new User());
//        return "Home";
//    }
    @GetMapping("/users/UI_Page")
    public String uiPage(Model model) {
        model.addAttribute("user", new User());
        return "Home"; // This should match your template name (Home.html)
    }


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
        return "Home";
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginSubmit(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        Optional<User> existingUser = userService.getUserByEmail(email);
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not registered.");
        } else if (!existingUser.get().getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
        }else {
            return ResponseEntity.ok("Login successful. Welcome "+existingUser.get().getName()+"!");
        }
    }

    @GetMapping
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    @ResponseBody
    public Optional<User> getUser(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("{id}")
    @ResponseBody
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            User existing = userOpt.get();
            existing.setName(user.getName());
            existing.setEmail(user.getEmail());
            existing.setUsername(user.getUsername());
            existing.setPassword(user.getPassword());
            return userService.saveUser(existing);
        }
        return null;
    }

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<User> signupAPI(@RequestBody User user) {
        Optional<User> existingUser = userService.getUserByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(409).build();
        }
        User saved = userService.saveUser(user);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("{id}")
    @ResponseBody
    public void deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
    }



}



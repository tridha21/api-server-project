package com.begin.user;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testLoginSuccess() throws Exception {
        User user = new User("Test", "test@example.com", "testuser", "pass");
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful. Welcome Test!"));
    }

    @Test
    void testUI_Page() throws Exception {
        mockMvc.perform(get("/users/UI_Page"))
                .andExpect(status().isOk())
                .andExpect(view().name("Home"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testSignupSubmit_NewUser() throws Exception {
        User user = new User("Test", "test@example.com", "testuser", "pass");
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users/signup")
                        .param("name", user.getName())
                        .param("email", user.getEmail())
                        .param("username", user.getUsername())
                        .param("password", user.getPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("Home"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void testSignupSubmit_ExistingUser() throws Exception {
        User user = new User("Test", "test@example.com", "testuser", "pass");
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/users/signup")
                        .param("name", user.getName())
                        .param("email", user.getEmail())
                        .param("username", user.getUsername())
                        .param("password", user.getPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("Home"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void testLoginInvalidPassword() throws Exception {
        User user = new User("Test", "test@example.com", "testuser", "pass");
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid password."));
    }

    @Test
    void testLoginEmailNotFound() throws Exception {
        when(userService.getUserByEmail("missing@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"missing@example.com\", \"password\":\"pass\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Email not registered."));
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<User> users = List.of(new User("A", "a@example.com", "a", "a"));
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User("A", "a@example.com", "a", "a");
        when(userService.getUserById(1)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateUser() throws Exception {
        User oldUser = new User("Old", "old@example.com", "old", "old");
        User newUser = new User("New", "new@example.com", "new", "new");

        when(userService.getUserById(1)).thenReturn(Optional.of(oldUser));
        when(userService.saveUser(any(User.class))).thenReturn(newUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    void testSignupAPI_NewUser() throws Exception {
        User user = new User("New", "new@example.com", "new", "pass");
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    void testSignupAPI_ExistingUser() throws Exception {
        User user = new User("New", "new@example.com", "new", "pass");
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict());
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUserById(1);
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}

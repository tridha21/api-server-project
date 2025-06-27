package com.begin.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

//@SpringBootTest
//class UserApplicationTests {
//
//    @Test
//    void contextLoads() {
//    }
//
//}

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserApplicationTests {
    @Test
    void contextLoads() {
    }
}


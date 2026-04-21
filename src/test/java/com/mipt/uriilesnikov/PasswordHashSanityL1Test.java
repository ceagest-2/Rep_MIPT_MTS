package com.mipt.uriilesnikov;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Tag("Layer1")
class PasswordHashSanityL1Test {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.security.users.user-password-hash}")
    private String userPasswordHash;

    @Test
    void configuredHashMatchesPasswordLiteral() {
        assertTrue(passwordEncoder.matches("password", userPasswordHash));
    }
}

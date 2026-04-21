package com.mipt.uriilesnikov.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @GetMapping("/profile")
    public Map<String, Object> profile(Authentication authentication) {
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities().stream().map(Object::toString).toList()
        );
    }

    @GetMapping("/docs")
    public Map<String, Object> docs() {
        return Map.of(
                "service", "Resilient Secure HTTP Gateway",
                "version", "v1",
                "note", "This endpoint requires READ_PRIVILEGE"
        );
    }
}

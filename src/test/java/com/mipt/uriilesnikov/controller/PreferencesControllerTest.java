package com.mipt.uriilesnikov.controller;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getViewPreference_shouldSetDefaultCookieWhenMissing() throws Exception {
        mockMvc.perform(get("/api/preferences/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("detailed"))
                .andExpect(header().string("Set-Cookie", containsString("viewPreference=detailed")));
    }

    @Test
    void getViewPreference_shouldReadExistingCookie() throws Exception {
        mockMvc.perform(get("/api/preferences/view")
                        .cookie(new Cookie("viewPreference", "compact")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("compact"));
    }

    @Test
    void setViewPreference_shouldUpdateCookie() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "compact"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("compact"))
                .andExpect(header().string("Set-Cookie", containsString("viewPreference=compact")));
    }

    @Test
    void setViewPreference_shouldFailForInvalidMode() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "invalid"))
                .andExpect(status().isBadRequest());
    }
}

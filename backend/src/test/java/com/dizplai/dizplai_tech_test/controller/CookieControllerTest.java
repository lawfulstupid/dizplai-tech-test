package com.dizplai.dizplai_tech_test.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CookieControllerTest {

    private MockMvc mvc;

    @InjectMocks
    private CookieController cookieController;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(cookieController).build();
    }

    @Test
    public void setCookieTest() throws Exception {
        mvc.perform(post("/cookie"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(CookieController.USER_ID_COOKIE));
    }

    @Test
    public void setCookieTest_existing() throws Exception {
        Cookie existingCookie = new Cookie(CookieController.USER_ID_COOKIE, "test");
        mvc.perform(post("/cookie").cookie(existingCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(CookieController.USER_ID_COOKIE));
    }

    @Test
    public void getCookieTest() throws Exception {
        Cookie existingCookie = new Cookie(CookieController.USER_ID_COOKIE, "test");
        mvc.perform(get("/cookie").cookie(existingCookie))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

    @Test
    public void getCookieTest_noCookie() throws Exception {
        mvc.perform(get("/cookie"))
                .andExpect(status().is4xxClientError());
    }

}

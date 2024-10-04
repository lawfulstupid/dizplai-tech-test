package com.dizplai.dizplai_tech_test.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(path = "/cookie")
@RequiredArgsConstructor
public class CookieController {

    public static final String USER_ID_COOKIE = "user-id";

    @PostMapping()
    public ResponseEntity<Void> setCookie(HttpServletResponse response, @CookieValue(value = USER_ID_COOKIE, required = false) String userId) {
        if (userId == null) {
            Cookie cookie = new Cookie(USER_ID_COOKIE, UUID.randomUUID().toString());
            cookie.setMaxAge(Integer.MAX_VALUE);
            cookie.setSecure(true);
            response.addCookie(cookie);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<String> getCookie(@CookieValue(USER_ID_COOKIE) String userId) {
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }

}

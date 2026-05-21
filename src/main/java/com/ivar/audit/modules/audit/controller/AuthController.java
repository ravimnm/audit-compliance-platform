package com.ivar.audit.modules.audit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ivar.audit.security.jwt.JwtTokenProvider;

@RestController
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/auth/token")
    public String getToken(@RequestParam String userId,
                           @RequestParam(defaultValue = "USER") String role) {

        return jwtTokenProvider.generateToken(userId); // role already hardcoded inside
    }
}
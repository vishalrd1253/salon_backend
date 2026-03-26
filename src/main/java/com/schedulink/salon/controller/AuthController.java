package com.schedulink.salon.controller;

import com.schedulink.salon.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestParam Long userId, @RequestParam String role) {

        return jwtUtil.generateToken(userId, role);
    }
}
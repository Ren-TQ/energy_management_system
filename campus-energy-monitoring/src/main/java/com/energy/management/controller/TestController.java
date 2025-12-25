package com.energy.management.controller;

import com.energy.management.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/validate-password")
    @PreAuthorize("permitAll()")
    public ApiResponse<Boolean> validatePassword(
            @RequestParam String rawPassword,
            @RequestParam String encodedPassword) {
        
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        return ApiResponse.success("密码验证结果", matches);
    }
}
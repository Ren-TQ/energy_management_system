package com.energy.management.controller;

import com.energy.management.dto.request.LoginRequest;
import com.energy.management.dto.request.RegisterRequest;
import com.energy.management.dto.response.ApiResponse;
import com.energy.management.dto.response.LoginResponse;
import com.energy.management.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录认证相关接口")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到登录请求: username={}", request.getUsername());
        try {
            LoginResponse response = authService.login(request);
            log.info("登录成功: username={}", request.getUsername());
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            log.error("登录失败: username={}, error={}", request.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResponse<Boolean> register(@Valid @RequestBody RegisterRequest request) {
        log.info("收到注册请求: username={}", request.getUsername());
        try {
            boolean result = authService.register(request);
            log.info("注册成功: username={}", request.getUsername());
            return ApiResponse.success("注册成功", result);
        } catch (Exception e) {
            log.error("注册失败: username={}, error={}", request.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public ApiResponse<?> logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            authService.logout(token);
        }
        return ApiResponse.success("登出成功", null);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
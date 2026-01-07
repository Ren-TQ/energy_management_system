package com.campus.energy.controller;

import com.campus.energy.dto.UserDTO;
import com.campus.energy.dto.auth.LoginRequest;
import com.campus.energy.dto.auth.LoginResponse;
import com.campus.energy.dto.common.Result;
import com.campus.energy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、注册和管理接口")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success("登录成功", authService.login(request));
    }
    
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<UserDTO> register(@Valid @RequestBody UserDTO dto) {
        return Result.success("注册成功", authService.register(dto));
    }
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取所有用户", description = "需要管理员权限")
    public Result<List<UserDTO>> getAllUsers() {
        return Result.success(authService.getAllUsers());
    }
    
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "根据ID获取用户", description = "需要管理员权限")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        return Result.success(authService.getUserById(id));
    }
    
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新用户信息", description = "需要管理员权限")
    public Result<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        return Result.success("更新成功", authService.updateUser(id, dto));
    }
    
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "需要管理员权限")
    public Result<Void> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return Result.success("删除成功", null);
    }
}


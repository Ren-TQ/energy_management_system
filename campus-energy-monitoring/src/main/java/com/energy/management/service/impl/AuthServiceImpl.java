package com.energy.management.service.impl;

import com.energy.management.dto.auth.LoginRequest;
import com.energy.management.dto.auth.LoginResponse;
import com.energy.management.dto.request.RegisterRequest;
import com.energy.management.entity.User;
import com.energy.management.exception.BusinessException;
import com.energy.management.repository.UserRepository;
import com.energy.management.security.JwtTokenProvider;
import com.energy.management.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        // 获取用户信息
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // 生成JWT Token
        String token = jwtTokenProvider.generateToken(authentication);
        
        log.info("用户登录成功: {}", request.getUsername());
        
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpiration())
                .userId(user.getId())
                .username(user.getUsername())
                .realName(null)
                .role(user.getRole().name())
                .roleLabel(getRoleLabel(user.getRole()))
                .build();
    }
    
    private String getRoleLabel(User.UserRole role) {
        if (role == User.UserRole.ADMIN) {
            return "管理员";
        } else if (role == User.UserRole.USER) {
            return "普通用户";
        }
        return role.name();
    }

    @Override
    public void logout(String token) {
        log.info("用户登出，token: {}", token);
    }

    @Override
    @Transactional
    public boolean register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在: " + request.getUsername());
        }
        
        // 检查邮箱是否已存在
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被使用: " + request.getEmail());
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(User.UserRole.USER);
        
        userRepository.save(user);
        log.info("用户注册成功: {}", user.getUsername());
        
        return true;
    }
}

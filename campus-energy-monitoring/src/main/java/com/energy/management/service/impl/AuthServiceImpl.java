package com.energy.management.service.impl;

import com.energy.management.dto.request.LoginRequest;
import com.energy.management.dto.request.RegisterRequest;
import com.energy.management.dto.response.LoginResponse;
import com.energy.management.entity.User;
import com.energy.management.repository.UserRepository;
import com.energy.management.security.JwtTokenProvider;
import com.energy.management.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            log.info("开始处理登录请求，用户名: {}", request.getUsername());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            User user = (User) authentication.getPrincipal();
            
            log.info("用户认证成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());

            // 更新最后登录时间 - 添加异常处理
            try {
                user.setLastLogin(LocalDateTime.now());
                User savedUser = userRepository.save(user);
                log.info("成功更新用户最后登录时间，用户ID: {}", savedUser.getId());
            } catch (Exception e) {
                log.warn("更新用户最后登录时间失败: {}", e.getMessage(), e);
                // 不中断登录流程，继续执行
            }

            LoginResponse response = new LoginResponse(
                    jwt,
                    user.getUsername(),
                    user.getRole().name(),
                    user.getId()
            );
            
            log.info("登录处理完成，生成响应");

            return response;

        } catch (AuthenticationException e) {
            log.error("认证失败: {}", e.getMessage());
            throw new RuntimeException("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录过程中发生未知错误: {}", e.getMessage(), e);
            throw new RuntimeException("登录服务暂时不可用");
        }
    }

    @Override
    public void logout(String token) {
        // 在实际项目中，这里可以将token加入黑名单
        log.info("用户登出，token: {}", token);
        SecurityContextHolder.clearContext();
    }

    @Override
    public boolean register(RegisterRequest request) {
        try {
            log.info("开始处理注册请求，用户名: {}", request.getUsername());

            // 检查用户名是否已存在
            if (userRepository.existsByUsername(request.getUsername())) {
                log.warn("用户名已存在: {}", request.getUsername());
                throw new RuntimeException("用户名已存在");
            }

            // 创建新用户
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setEmail(request.getEmail());
            
            // 设置角色，只允许注册普通用户，管理员需要特殊处理
            if ("ADMIN".equals(request.getRole())) {
                log.warn("不允许普通注册成为管理员用户: {}", request.getUsername());
                newUser.setRole(User.UserRole.USER); // 强制设为普通用户
            } else {
                newUser.setRole(User.UserRole.USER);
            }
            
            newUser.setCreatedAt(LocalDateTime.now());

            // 保存用户
            userRepository.save(newUser);
            log.info("用户注册成功，用户ID: {}, 用户名: {}", newUser.getId(), newUser.getUsername());
            
            return true;
        } catch (Exception e) {
            log.error("注册过程中发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("注册失败: " + e.getMessage());
        }
    }
}
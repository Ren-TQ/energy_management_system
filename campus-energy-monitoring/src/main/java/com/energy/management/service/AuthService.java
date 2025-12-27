package com.energy.management.service;

import com.energy.management.dto.UserDTO;
import com.energy.management.dto.auth.LoginRequest;
import com.energy.management.dto.auth.LoginResponse;
import com.energy.management.entity.User;
import com.energy.management.enums.UserRole;
import com.energy.management.exception.BusinessException;
import com.energy.management.repository.UserRepository;
import com.energy.management.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        String token = jwtTokenProvider.generateToken(authentication);
        
        log.info("用户登录成功: {}", request.getUsername());
        
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpiration())
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .roleLabel(user.getRole().getLabel())
                .build();
    }
    
    @Transactional
    public UserDTO register(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("用户名已存在: " + dto.getUsername());
        }
        
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("邮箱已被使用: " + dto.getEmail());
        }
        
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .realName(dto.getRealName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .role(dto.getRole() != null ? dto.getRole() : UserRole.USER)
                .enabled(true)
                .build();
        
        user = userRepository.save(user);
        log.info("用户注册成功: {}", user.getUsername());
        
        return convertToDTO(user);
    }
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在，ID: " + id));
        return convertToDTO(user);
    }
    
    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在，ID: " + id));
        
        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getEnabled() != null) {
            user.setEnabled(dto.getEnabled());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        user = userRepository.save(user);
        log.info("用户信息更新成功: {}", user.getUsername());
        
        return convertToDTO(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在，ID: " + id));
        
        userRepository.delete(user);
        log.info("用户删除成功: {}", user.getUsername());
    }
    
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .roleLabel(user.getRole().getLabel())
                .enabled(user.getEnabled())
                .lastLoginTime(user.getLastLoginTime())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

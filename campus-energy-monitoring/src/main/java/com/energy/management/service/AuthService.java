package com.energy.management.service;

import com.energy.management.dto.auth.LoginRequest;
import com.energy.management.dto.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
    boolean register(com.energy.management.dto.request.RegisterRequest request);
}

package com.energy.management.service;

import com.energy.management.dto.request.LoginRequest;
import com.energy.management.dto.request.RegisterRequest;
import com.energy.management.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
    boolean register(RegisterRequest request);
}
package com.campus.energy.controller;

import com.campus.energy.dto.UserDTO;
import com.campus.energy.dto.auth.LoginRequest;
import com.campus.energy.dto.auth.LoginResponse;
import com.campus.energy.dto.common.Result;
import com.campus.energy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器
 * 
 * <p>负责处理用户认证和用户管理相关的HTTP请求，提供登录、注册、用户管理等功能。
 * 本控制器是系统的入口，所有用户操作都需要先通过认证。</p>
 * 
 * <p><b>功能说明：</b></p>
 * <ul>
 *   <li>认证功能：用户登录、用户注册</li>
 *   <li>用户管理：查询用户列表、查询用户详情、更新用户信息、删除用户（需要管理员权限）</li>
 * </ul>
 * 
 * <p><b>权限说明：</b></p>
 * <ul>
 *   <li>登录/注册接口：无需认证，公开访问</li>
 *   <li>用户管理接口：需要管理员（ADMIN）角色</li>
 * </ul>
 * 
 * <p><b>安全机制：</b></p>
 * <ul>
 *   <li>使用JWT Token进行身份认证</li>
 *   <li>密码使用BCrypt加密存储</li>
 *   <li>登录成功后返回Token，后续请求需要在Header中携带Token</li>
 * </ul>
 * 
 * <p><b>请求路径：</b>/api/auth</p>
 * 
 * @author Campus Energy System
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、注册和管理接口")
public class AuthController {
    
    /** 认证服务层，处理用户认证和用户管理相关的业务逻辑 */
    private final AuthService authService;
    
    /**
     * 用户登录
     * 
     * <p>验证用户身份并返回JWT Token。登录成功后，客户端需要在后续请求的
     * Authorization Header中携带该Token进行身份验证。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>POST /api/auth/login
     * Content-Type: application/json
     * 
     * {
     *   "username": "admin",
     *   "password": "123456"
     * }</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>username：用户名，必填，字符串类型</li>
     *   <li>password：密码，必填，字符串类型</li>
     * </ul>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "登录成功",
     *   "data": {
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *     "tokenType": "Bearer",
     *     "expiresIn": 86400000,
     *     "user": {
     *       "id": 1,
     *       "username": "admin",
     *       "realName": "系统管理员",
     *       "role": "ADMIN"
     *     }
     *   },
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>用户名或密码错误：返回401未授权错误</li>
     *   <li>用户被禁用：返回403禁止访问错误</li>
     *   <li>参数验证失败：返回400错误</li>
     * </ul>
     * 
     * <p><b>使用说明：</b></p>
     * <ul>
     *   <li>登录成功后，将返回的Token保存到本地（如localStorage）</li>
     *   <li>后续API请求需要在Header中添加：Authorization: Bearer {token}</li>
     *   <li>Token有过期时间，过期后需要重新登录</li>
     * </ul>
     * 
     * @param request 登录请求对象，包含用户名和密码，通过@Valid注解进行参数校验
     * @return 包含JWT Token和用户信息的Result对象
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户身份并返回JWT Token，用于后续API请求的身份验证")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success("登录成功", authService.login(request));
    }
    
    /**
     * 用户注册
     * 
     * <p>在系统中注册新的用户账号。注册成功后，用户可以使用注册的用户名和密码进行登录。
     * 新注册的用户默认角色为普通用户（USER）。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>POST /api/auth/register
     * Content-Type: application/json
     * 
     * {
     *   "username": "newuser",
     *   "password": "123456",
     *   "realName": "新用户",
     *   "email": "newuser@campus.edu",
     *   "phone": "13800000003"
     * }</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>username：用户名，必填，必须唯一</li>
     *   <li>password：密码，必填，建议至少6位</li>
     *   <li>realName：真实姓名，可选</li>
     *   <li>email：邮箱，可选</li>
     *   <li>phone：手机号，可选</li>
     * </ul>
     * 
     * <p><b>验证规则：</b></p>
     * <ul>
     *   <li>用户名不能重复</li>
     *   <li>密码长度建议至少6位</li>
     *   <li>邮箱格式验证（如果提供）</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>用户名已存在：返回400错误</li>
     *   <li>参数验证失败：返回400错误</li>
     * </ul>
     * 
     * @param dto 用户信息DTO，包含注册所需的用户信息，通过@Valid注解进行参数校验
     * @return 包含新注册用户信息的Result对象
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "在系统中注册新的用户账号，默认角色为普通用户")
    public Result<UserDTO> register(@Valid @RequestBody UserDTO dto) {
        return Result.success("注册成功", authService.register(dto));
    }
    
    /**
     * 获取所有用户列表
     * 
     * <p>查询系统中所有已注册的用户信息，包括管理员和普通用户。
     * 仅管理员可以访问此接口。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/auth/users
     * Authorization: Bearer {token}</pre>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": [
     *     {
     *       "id": 1,
     *       "username": "admin",
     *       "realName": "系统管理员",
     *       "role": "ADMIN",
     *       "enabled": true
     *     }
     *   ],
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>返回的用户信息不包含密码</li>
     *   <li>仅管理员可以访问此接口</li>
     * </ul>
     * 
     * @return 包含所有用户信息的Result对象，data字段为用户列表
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取所有用户", description = "查询系统中所有用户信息，需要管理员权限")
    public Result<List<UserDTO>> getAllUsers() {
        return Result.success(authService.getAllUsers());
    }
    
    /**
     * 根据用户ID获取用户详细信息
     * 
     * <p>通过用户唯一标识ID查询指定用户的完整信息，包括用户名、真实姓名、
     * 邮箱、手机号、角色、启用状态等。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/auth/users/1
     * Authorization: Bearer {token}</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：用户ID，路径参数，必填，Long类型</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>用户不存在：返回404错误</li>
     * </ul>
     * 
     * @param id 用户ID，从URL路径中获取
     * @return 包含用户详细信息的Result对象
     */
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "根据ID获取用户", description = "通过用户ID查询用户的详细信息，需要管理员权限")
    public Result<UserDTO> getUserById(
            @Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id) {
        return Result.success(authService.getUserById(id));
    }
    
    /**
     * 更新用户信息
     * 
     * <p>更新指定用户的详细信息。可以修改真实姓名、邮箱、手机号、角色、启用状态等信息。
     * 注意：用户名和密码通常需要单独接口进行修改。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>PUT /api/auth/users/1
     * Authorization: Bearer {token}
     * Content-Type: application/json
     * 
     * {
     *   "realName": "更新后的姓名",
     *   "email": "newemail@campus.edu",
     *   "phone": "13900000000",
     *   "role": "USER",
     *   "enabled": true
     * }</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：用户ID，路径参数，必填</li>
     *   <li>dto：更新的用户信息DTO，请求体参数，必填</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>用户不存在：返回404错误</li>
     *   <li>参数验证失败：返回400错误</li>
     * </ul>
     * 
     * @param id 用户ID，从URL路径中获取
     * @param dto 更新的用户信息DTO
     * @return 包含更新后用户信息的Result对象
     */
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新用户信息", description = "更新指定用户的详细信息，需要管理员权限")
    public Result<UserDTO> updateUser(
            @Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id, 
            @RequestBody UserDTO dto) {
        return Result.success("更新成功", authService.updateUser(id, dto));
    }
    
    /**
     * 删除用户
     * 
     * <p>从系统中永久删除指定的用户账号。删除操作不可恢复，请谨慎操作。
     * 通常建议使用禁用（enabled=false）而不是删除用户。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>DELETE /api/auth/users/1
     * Authorization: Bearer {token}</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：用户ID，路径参数，必填</li>
     * </ul>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>删除操作不可恢复，请确认后再执行</li>
     *   <li>建议优先使用禁用用户功能，而不是删除</li>
     *   <li>不能删除当前登录的管理员自己</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>用户不存在：返回404错误</li>
     *   <li>不能删除自己：返回400错误</li>
     * </ul>
     * 
     * @param id 用户ID，从URL路径中获取
     * @return 操作成功的Result对象，data字段为null
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "从系统中删除指定的用户账号（不可恢复），需要管理员权限")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id) {
        authService.deleteUser(id);
        return Result.success("删除成功", null);
    }
}


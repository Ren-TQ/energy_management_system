package com.energy.management.dto.auth;

import com.energy.management.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {
    
    @Schema(description = "JWT Token")
    private String token;
    
    @Schema(description = "Token类型")
    @Builder.Default
    private String tokenType = "Bearer";
    
    @Schema(description = "过期时间(毫秒)")
    private Long expiresIn;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "用户角色")
    private UserRole role;
    
    @Schema(description = "角色描述")
    private String roleLabel;
}


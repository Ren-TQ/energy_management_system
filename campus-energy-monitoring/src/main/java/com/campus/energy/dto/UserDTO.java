package com.campus.energy.dto;

import com.campus.energy.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息")
public class UserDTO {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @Schema(description = "密码(创建/修改时使用)")
    private String password;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "用户角色")
    private UserRole role;
    
    @Schema(description = "角色描述")
    private String roleLabel;
    
    @Schema(description = "账户是否启用")
    private Boolean enabled;
    
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}


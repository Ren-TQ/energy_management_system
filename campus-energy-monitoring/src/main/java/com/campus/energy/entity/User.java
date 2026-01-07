package com.campus.energy.entity;

import com.campus.energy.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ============================================
 * 领域模型类型：贫血模型（Anemic Domain Model）
 * ============================================
 * 
 * 模型说明：
 * 本类采用贫血模型设计，只包含数据字段，不包含业务逻辑方法。
 * 所有业务逻辑（如用户认证、权限验证）都在AuthService层处理。
 * ============================================
 * 
 * 用户实体类
 * 用于存储系统用户信息
 */
@Entity
@Table(name = "t_user", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"username"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * 密码 (加密存储)
     */
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    
    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;
    
    /**
     * 邮箱
     */
    @Column(name = "email", length = 100)
    private String email;
    
    /**
     * 手机号
     */
    @Column(name = "phone", length = 20)
    private String phone;
    
    /**
     * 用户角色
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;
    
    /**
     * 账户是否启用
     */
    @Column(name = "enabled")
    @Builder.Default
    private Boolean enabled = true;
    
    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


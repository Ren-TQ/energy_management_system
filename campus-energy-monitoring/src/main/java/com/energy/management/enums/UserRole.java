package com.energy.management.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    
    ADMIN("管理员", "拥有所有权限，可进行系统管理"),
    USER("普通用户", "仅能查看数据，无法进行修改操作");
    
    private final String label;
    private final String description;
    
    UserRole(String label, String description) {
        this.label = label;
        this.description = description;
    }
}


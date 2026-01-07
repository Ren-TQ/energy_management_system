package com.campus.energy.enums;

import lombok.Getter;

/**
 * 设备通讯状态枚举
 */
@Getter
public enum DeviceStatus {
    
    ONLINE("在线", "设备正常运行中"),
    OFFLINE("离线", "设备断开连接"),
    MAINTENANCE("维护中", "设备正在维护"),
    DECOMMISSIONED("已停用", "设备已停用或拆除");
    
    private final String label;
    private final String description;
    
    DeviceStatus(String label, String description) {
        this.label = label;
        this.description = description;
    }
}

